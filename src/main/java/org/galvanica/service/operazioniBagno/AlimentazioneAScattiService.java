package org.galvanica.service.operazioniBagno;

import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.OggettoAggiunta;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.ScattiMath;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.*;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.galvanica.math.MetodiArrotondamenti.convertiQuantitaPerDto;
import static org.galvanica.math.MetodiArrotondamenti.convertiUnitaMisuraPerDto;

@Service
public class AlimentazioneAScattiService {

    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final BagnoService bagnoService;


    public AlimentazioneAScattiService(
            StoricoGeneraleRepository storicoGeneraleRepository,
            StoricoDettaglioRepository storicoDettaglioRepository,
            BagnoService bagnoService) {
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.bagnoService = bagnoService;
    }

    public List<AlimentazioneRisposta> calcolaAlimentazioneList(
            Map<Long, Integer> idBagnoScattiParzialiMap) {
        List<AlimentazioneRisposta> risposta = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : idBagnoScattiParzialiMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            risposta.add(calcolaAlimentazioneNuovo(entry.getKey(),
                    entry.getValue()));

        }
        return risposta;
    }

    //calcola nuova Alimentazione
    public AlimentazioneRisposta calcolaAlimentazioneNuovo(Long idBagno,
                                                           int scattiAttuali) {
        calcolaAlimentazioneControlliApprovati(idBagno);
        Bagno bagno = bagnoService.modelRicercaId(idBagno);
        Alimentazione alimentazione = trovaAlimentazioneScatti(bagno);
        StoricoGenerale ultimoStoricoGenerale = storicoGeneraleRepository.ultimoStoricoGeneraleScatti(
                idBagno);
        int scattiParziali = ultimoStoricoGenerale.getRestoScatti() + scattiAttuali;
        Integer primoValoreVolumetrico = trovaPrimoValoreVolumetrico(alimentazione);
        ScattiMath scattiMath = MetodiArrotondamenti.alimentazioneScattiMath(
                scattiAttuali,
                alimentazione.getScatti(),
                alimentazione.getArrotondaValori(),
                primoValoreVolumetrico);
        Integer scattiTotali = ultimoStoricoGenerale.getScattiTotali() + scattiAttuali;
        double moltiplicatoreAlimentazione = 0D;
        String messaggio = "gli scatti sono inferiori al 90% dell'alimentazione," +
                "le aggiunte non verranno eseguite ma messe in conto per la prossima chiamata.";
        List<Long> idDettaglioList = null;
        List<OggettoAggiunta> oggettoAggiuntaList = null;
        Long idStoricoGenerale;

        if (scattiMath.getMoltiplicatoreAlimentazione() == 0) {
            StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                    StoricoGenerale.builder()
                            .bagno(bagno)
                            .scattiTotali(scattiTotali)
                            .scattiInseriti(scattiAttuali)
                            .alimentazione(alimentazione)
                            .tipologiaAggiunta(TipologiaAggiunta.SCATTI)
                            .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                            .build());
            idStoricoGenerale = storicoGenerale.getIdStorico();

            //todo: gestire StoricoGenerale affinchè se trova storicoGenerale
            // senza StoricoDettaglio automaticamente lo conferma.
        } else {
            //altrimenti (se c'è alimentazione).
            // A: crea nuovo storicoGenerale
            StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                    StoricoGenerale.builder()
                            .bagno(bagno)
                            .scattiTotali(scattiTotali)
                            .restoScatti((int) scattiMath.getRestoScatti())
                            .scattiInseriti(scattiAttuali)
                            .alimentazione(alimentazione)
                            .tipologiaAggiunta(TipologiaAggiunta.SCATTI)
                            .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                            .build());

            // B: crea gli storicoDettaglio per ogni dettaglioAlimentazione trovato.
            creaStoricoDettaglioApprossimato(
                    alimentazione,
                    scattiMath,
                    storicoGenerale);

            storicoGenerale = trovaStoricoGenerale(storicoGenerale.getIdStorico());
            oggettoAggiuntaList = aggiunteAttuali(storicoGenerale.getIdStorico());
            idDettaglioList = oggettoAggiuntaList.stream()
                    .flatMap(oggettoAggiunta -> oggettoAggiunta.getIdStoricoDettaglioList()
                            .stream()).toList();
            moltiplicatoreAlimentazione = scattiMath.getMoltiplicatoreAlimentazione();
            scattiParziali = (int) scattiMath.getRestoScatti();
            messaggio = "questa è l'aggiunta a scatti che prevede il bagno";
            idStoricoGenerale = storicoGenerale.getIdStorico();
        }

        return AlimentazioneRisposta.builder()
                .idBagno(idBagno)
                .nomeBagno(bagno.getNome())
                //TODO: verificare che basti in AlimentazioneRisposta aggiuntaDaStoriciPassatiList
                // e non debba essere implementata scattiConteggiProdotti.
                .moltiplicatoreAlimentazione(moltiplicatoreAlimentazione)
                .restoScatti(scattiParziali)
                .scattiAlimentazione(alimentazione.getScatti())
                .scattiTotali(scattiTotali)
                .scattiInseriti(scattiAttuali)
                .scattiParzialiPrecedenti(ultimoStoricoGenerale.getRestoScatti())
                .idStoricoGenerale(idStoricoGenerale)
                .messaggio(messaggio)
                .idStoricoDettaglioList(idDettaglioList)
                .oggettoAggiuntaList(oggettoAggiuntaList)
                .build();
    }

    private Integer trovaPrimoValoreVolumetrico(Alimentazione alimentazione) {

        return alimentazione
                .getDettaglioAlimentazioneList()
                .stream()
                .filter(dettaglioAlimentazione -> dettaglioAlimentazione.getUnitaDiMisura()
                        .isSonoVolume())
                .findFirst()
                .map(DettaglioAlimentazione::getQuantitaProdotto)
                .orElse(null);
    }

    private void creaStoricoDettaglioApprossimato(
            Alimentazione alimentazione,
            ScattiMath scattiMath,
            StoricoGenerale storicoGenerale) {
        for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {
            double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto()
                    * scattiMath.getMoltiplicatoreAlimentazione();
            if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
                quantitaProdottoAggiunta = MetodiArrotondamenti.moltiplicatoreApprossimatoPerAggiunta(
                        quantitaProdottoAggiunta);
            }
            storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(storicoGenerale)
                            .quantita((int) Math.round(quantitaProdottoAggiunta))
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build());
        }

    }

    private List<OggettoAggiunta> aggiunteAttuali(
            Long idStoricoGenerale) {

        List<OggettoAggiunta> oggettoAggiuntaList = new ArrayList<>();
        List<StoricoDettaglio> storicoDettaglioList = storicoDettaglioRepository.storicoDettaglioList(
                idStoricoGenerale,
                false,
                false);
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            oggettoAggiuntaList.add(oggettoAggiuntaTrasformer(storicoDettaglio));
        }
        return oggettoAggiuntaList;
    }


    //calcola tutte le aggiunte da Storico

    public AlimentazioneRisposta calcolaAggiunteStorico(Long idBagno) {
        calcolaAlimentazioneControlliApprovati(idBagno);
        Bagno bagno = bagnoService.modelRicercaId(idBagno);
        Alimentazione alimentazione = trovaAlimentazioneScatti(bagno);
        StoricoGenerale ultimoStoricoGenerale = storicoGeneraleRepository.ultimoStoricoGeneraleScatti(
                idBagno);
        String messaggio = "Queste sono tutte le aggiunte non ancora effettuate per il bagno.";
        List<OggettoAggiunta> oggettoAggiuntaList = tutteLeAggiunteDaFareBagnoList(
                idBagno);
        List<Long> idDettaglioList = oggettoAggiuntaList.stream()
                .flatMap(oggettoAggiunta -> oggettoAggiunta.getIdStoricoDettaglioList()
                        .stream()).toList();

        return AlimentazioneRisposta.builder()
                .idBagno(idBagno)
                .nomeBagno(bagno.getNome())
                .restoScatti(ultimoStoricoGenerale.getRestoScatti())
                .scattiAlimentazione(alimentazione.getScatti())
                .scattiTotali(ultimoStoricoGenerale.getScattiTotali())
                .scattiInseriti(0)
                .messaggio(messaggio)
                .idStoricoDettaglioList(idDettaglioList)
                .oggettoAggiuntaList(oggettoAggiuntaList)
                .build();
    }

    private List<OggettoAggiunta> tutteLeAggiunteDaFareBagnoList(Long idBagno) {
        //crea una mappa di idProdotto e oggettoAggiunta.

        Map<Long, OggettoAggiunta> oggettoAggiuntaMap = new HashMap<>();
        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, TipologiaAggiunta.SCATTI);
        /*ricerca uno storico dettaglio:
        1.filtra storicoGeneraleList
        2.recupero lo storicoDettaglio
        3.se non c'è faccio storicoDettaglioRepository.storicoDettaglioList(
                            s.getIdStorico(),
                            false,
                            false); (ps--per lazy!)
        4.una volta ottenute n. liste di storicoDettaglio dalle liste di StoricoGenerale
        le riduco tutte ad un unica lista di StoricoDettaglio (flatMap(Collection::parallelStream)
        5. tale lista unica ottenuta viene filtrata per: tutti i valori !eseguti e !esclusi.
        6.riporto infine la lista.

        attenzione! i return riportano il valore dentro la funzione al valore sotto (qui .flatmap)
         e non al valore List<StoricoDettaglio> storicoDettaglioList

         */
        List<StoricoDettaglio> storicoDettaglioList = storicoGeneraleListDaEseguire.stream()
                .map(s -> {
                    //todo: verifica se possibile pulzia codice per lazy!
                    if (s.getStoricoDettaglioList() != null) {
                        return s.getStoricoDettaglioList();
                    }
                    return storicoDettaglioRepository.storicoDettaglioList(
                            s.getIdStorico(),
                            false,
                            false);
                })

                .flatMap(Collection::parallelStream)
                .filter(storicoDettaglio -> !storicoDettaglio.getEseguito() && !storicoDettaglio.getEscluso())
                .toList();


        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {

            if (oggettoAggiuntaMap.containsKey(storicoDettaglio.getProdotto()
                    .getIdProdotto())) {
                Double quantitaProdotto = oggettoAggiuntaMap.get(storicoDettaglio.getProdotto()
                        .getIdProdotto()).getQuantitaProdotto()
                        + storicoDettaglio.getQuantita();
                oggettoAggiuntaMap.get(storicoDettaglio.getProdotto()
                        .getIdProdotto()).setQuantitaProdotto(quantitaProdotto);
                oggettoAggiuntaMap.get(storicoDettaglio.getProdotto()
                                .getIdProdotto())
                        .getIdStoricoDettaglioList()
                        .add(storicoDettaglio.getIdStoricoDettaglio());
            }
            if (!oggettoAggiuntaMap.containsKey(storicoDettaglio.getProdotto()
                    .getIdProdotto())) {
                oggettoAggiuntaMap.put(storicoDettaglio.getProdotto()
                                .getIdProdotto(),
                        oggettoAggiuntaTrasformer(storicoDettaglio));
            }
        }
        return new ArrayList<>(oggettoAggiuntaMap.values());
    }

    //metodi comuni

    private void calcolaAlimentazioneControlliApprovati(Long idBagno) {
        //todo:impostare i controlli a monte di tutti i metodi
        Bagno bagno = bagnoService.modelRicercaId(idBagno);
        if (bagno.getAlimentazioneList().isEmpty()) {
            throw new RuntimeException(
                    "Alimentazione a scatti non trovata per bagno " + idBagno);
        }
        if (storicoGeneraleRepository.ultimoStoricoGeneraleScatti(idBagno) == null) {
            throw new RuntimeException(
                    "Non vi è nessuno storicoGenerale relativo agli scatti per il bagno " + idBagno);
        }
    }


    private StoricoGenerale trovaStoricoGenerale(long id) {
        Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(
                id);
        return storicoGeneraleTrovato.orElseThrow();
    }

    private Alimentazione trovaAlimentazioneScatti(Bagno bagno) {
        return bagno.getAlimentazioneList()
                .stream()
                .filter(alimentazioneFilter -> alimentazioneFilter.getScatti() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Alimentazione a scatti non trovata per bagno " + bagno.getIdBagno() + " nome: " + bagno.getNome()));
    }

    private OggettoAggiunta oggettoAggiuntaTrasformer(StoricoDettaglio dettaglio) {
        Double quantitaProdotto = convertiQuantitaPerDto(
                dettaglio.getQuantita(),
                dettaglio.getUnitaDiMisura().isSonoVolume());
        UnitaDiMisura unita = convertiUnitaMisuraPerDto(
                dettaglio.getQuantita(),
                dettaglio.getUnitaDiMisura().isSonoVolume());
        List<Long> list = new ArrayList<>();
        list.add(dettaglio.getIdStoricoDettaglio());
        return OggettoAggiunta.builder()
                .unitaDiMisura(unita)
                .quantitaProdotto(quantitaProdotto)
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .nomeProdotto(dettaglio.getProdotto().getNome())
                .idStoricoDettaglioList(list)
                .build();
    }


}
