package org.galvanica.service.operazioniBagno;

import jakarta.persistence.EntityManager;
import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.OggettoAggiunta;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.ScattiMath;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.*;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static org.galvanica.math.MetodiArrotondamenti.convertiQuantitaPerDto;
import static org.galvanica.math.MetodiArrotondamenti.convertiUnitaMisuraPerDto;

@Service
public class OperazioniAddStoricoProva {

    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final AlimentazioneRepository alimentazioneRepository;

    private final EntityManager entityManager;

    private Boolean scattiCalcolaAlimentazioneControlliApprovati(Long idBagno) {
        //todo:impostare i controlli a monte di tutti i metodi
        if (bagnoRepository.findById(idBagno).isEmpty()) {
            throw new RuntimeException(
                    "bagno non trovato per id " + idBagno);
        }
        if (bagnoRepository.findById(idBagno)
                .get()
                .getAlimentazioneList()
                .isEmpty()) {
            throw new RuntimeException(
                    "Alimentazione a scatti non trovata per bagno " + idBagno);
        }
        return true;
    }

    public AlimentazioneRisposta scattiCalcolaAlimentazioneNuovo(Long idBagno,
                                                                 int scattiParzialiPrecedenti,
                                                                 int scattiAttuali) {
        //todo: metodo che da il calcolo di quantita etc degli scatti attuali SENZA storico.
        // quindi, se necessario, crea lo storico.
        if (!scattiCalcolaAlimentazioneControlliApprovati(idBagno)) {
            throw new RuntimeException(
                    "alcuni dei controlli non sono stati passati. verificare.");
        }
        //trova bagno, alimentazione e storico
        Bagno bagno = trovaBagno(idBagno);
        Alimentazione alimentazione = scattiTrovaAlimentazione(bagno);
        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, true);
        int scattiParziali = scattiParzialiPrecedenti + scattiAttuali;
        Integer primoValoreVolumetrico = trovaPrimoValoreVolumetrico(alimentazione);
        ScattiMath scattiMath = MetodiArrotondamenti.alimentazioneScattiMath(
                scattiAttuali,
                alimentazione.getScatti(),
                alimentazione.getArrotondaValori(),
                primoValoreVolumetrico);
        Integer scattiTotali = scattiScattiTotali(scattiParziali,
                bagno,
                storicoGeneraleListDaEseguire);

        if (scattiMath.getMoltiplicatoreAlimentazione() == 0) {
            System.out.println(
                    "il bagno non richiede aggiunte per ora. nuovo resto scatti : " + scattiAttuali);
            StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                    StoricoGenerale.builder()
                            .bagno(bagno)
                            .scattiTotali(scattiTotali)
                            .scattiInseriti(scattiAttuali)
                            .alimentazione(alimentazione)
                            .sonoScatti(true)
                            .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                            .build());

            //todo: gestire StoricoGenerale affinchè se trova storicoGenerale
            // senza StoricoDettaglio automaticamente lo conferma.

            return AlimentazioneRisposta.builder()
                    .idBagno(idBagno)
                    .nomeBagno(bagno.getNome())
                    .moltiplicatoreAlimentazione(0D)
                    .restoScatti(scattiParziali)
                    .scattiAlimentazione(alimentazione.getScatti())
                    .scattiTotali(scattiTotali)
                    .scattiInseriti(scattiAttuali)
                    .scattiParzialiPrecedenti(scattiParzialiPrecedenti)
                    .idStoricoGenerale(storicoGenerale.getIdStorico())
                    .messaggio("gli scatti sono inferiori al 90% dell'alimentazione,"
                            + "le aggiunte non verranno eseguite ma messe in conto per la prossima chiamata.")
                    .build();
        }
        //altrimenti (se c'è alimentazione).
        // A: crea nuovo storicoGenerale
        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .bagno(bagno)
                        .scattiTotali(scattiTotali)
                        .restoScatti((int) scattiMath.getRestoScatti())
                        .scattiInseriti(scattiAttuali)
                        .alimentazione(alimentazione)
                        .sonoScatti(true)
                        .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                        .build());

        // B: crea gli storicoDettaglio per ogni dettaglioAlimentazione trovato.
        scattiConteggiProdotti(
                alimentazione,
                scattiMath,
                storicoGenerale);
        storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, true);
        List<OggettoAggiunta> oggettoAggiuntaList = aggiunteAttualiList(
                storicoGeneraleListDaEseguire);
        List<Long> idDettaglioList = oggettoAggiuntaList.stream()
                .flatMap(oggettoAggiunta -> oggettoAggiunta.getIdStoricoDettaglioList()
                        .stream()).toList();

        return AlimentazioneRisposta.builder()
                .idBagno(idBagno)
                .nomeBagno(bagno.getNome())
                //TODO: verificare che basti in AlimentazioneRisposta aggiuntaDaStoriciPassatiList
                // e non debba essere implementata scattiConteggiProdotti.
                .oggettoAggiuntaList(oggettoAggiuntaList)
                .idStoricoGenerale(storicoGenerale.getIdStorico())
                .idStoricoDettaglioList(idDettaglioList)
                .restoScatti((int) scattiMath.getRestoScatti())
                .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                .build();


    }

    private List<OggettoAggiunta> aggiunteAttualiList(
            List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        //crea una mappa di idProdotto e oggettoAggiunta.
        Map<Long, OggettoAggiunta> oggettoAggiuntaMap = new HashMap<>();

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

    public int scattiScattiParziali(int scatti, Bagno bagno,
                                    List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        int scattiParziali = scatti;
        boolean risposta = !storicoGeneraleListDaEseguire.isEmpty();
        if (risposta) {
            scattiParziali = storicoGeneraleListDaEseguire.getFirst()
                    .getRestoScatti()
                    + scatti;
        } else {
            scattiParziali = bagno.getRestoScatti() + scattiParziali;
        }
        return scattiParziali;
    }

    public int scattiScattiTotali(int scattiParziali, Bagno bagno,
                                  List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        int scattiTotali;
        boolean risposta = !storicoGeneraleListDaEseguire.isEmpty();
        if (risposta) {
            scattiTotali = storicoGeneraleListDaEseguire.getFirst().getScattiTotali()
                    + scattiParziali;
            //altrimenti prendi scatti dal bagno (già aggiornato)
        } else {
            scattiTotali = bagno.getScattiTotali() + scattiParziali;
        }
        return scattiTotali;
    }


    public AlimentazioneRisposta scattiCalcolaAlimentazioneArchivio(Long idBagno) {
        //todo: metodo che somma tutti gli scatti dell'archivio del bagno.
        return null;
    }


    //todo: attenzione, quando si confermano le aggiunte storico precedenti vanno approvate in ordine crescente di data, mai al contrario o non tornano gli scatti totali e parziali
//todo: attenzione! in questo momentoo viene ricercato su StoricoGenerale tutte gli storici (sia con il parametro scatti che con il parametro tempo), da implementare controllo!
    public OperazioniAddStoricoProva(BagnoRepository bagnoRepository,
                                     StoricoGeneraleRepository storicoGeneraleRepository,
                                     StoricoDettaglioRepository storicoDettaglioRepository,
                                     AlimentazioneRepository alimentazioneRepository,
                                     EntityManager entityManager) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.alimentazioneRepository = alimentazioneRepository;
        this.entityManager = entityManager;
    }

    //fai scattiCalcolaAlimentazione ma per una mappa di bagno/valore e riporta una lista.
    public List<AlimentazioneRisposta> scattiCalcolaAlimentazioneList(
            Map<Long, Integer> idBagnoScattiParzialiMap) {
        List<AlimentazioneRisposta> risposta = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : idBagnoScattiParzialiMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            risposta.add(scattiCalcolaAlimentazione(entry.getKey(),
                    entry.getValue()));

        }
        return risposta;
    }

    //crea storico dettaglio con approssimazioni di misura per volume
    private List<StoricoDettaglio> scattiConteggiProdotti(
            Alimentazione alimentazione,
            ScattiMath scattiMath,
            StoricoGenerale storicoGenerale) {
        List<StoricoDettaglio> storicoDettaglioList = new ArrayList<>();
        for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {
            Double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto()
                    * scattiMath.getMoltiplicatoreAlimentazione();
            if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
                quantitaProdottoAggiunta = MetodiArrotondamenti.moltiplicatoreApprossimatoPerAggiunta(
                        quantitaProdottoAggiunta);
            }
            //todo: potrebbe fare casini??????? farà bene gli arrotondamenti?
            StoricoDettaglio storicoDettaglio = storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(trovaStoricoGenerale(storicoGenerale.getIdStorico()))
                            .quantita((int) Math.round(quantitaProdottoAggiunta))
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build());

            storicoDettaglioList.add(storicoDettaglio);
        }
        return storicoDettaglioList;

    }


    private List<OggettoAggiunta> aggiuntaDaStoriciPassatiList(
            List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        //crea una mappa di idProdotto e oggettoAggiunta.
        Map<Long, OggettoAggiunta> oggettoAggiuntaMap = new HashMap<>();

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

    private Alimentazione scattiTrovaAlimentazione(Bagno bagno) {
        return bagno.getAlimentazioneList()
                .stream()
                .filter(alimentazioneFilter -> alimentazioneFilter.getScatti() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Alimentazione a scatti non trovata per bagno " + bagno.getIdBagno() + " nome: " + bagno.getNome()));
    }


    public AlimentazioneRisposta tempoCalcolaAlimentazione(
            Long idBagno, LocalDate dataControllo) {
        StoricoGenerale storicoGeneraleTempoLast =
                storicoGeneraleRepository.storicoGeneraleTempoLast(idBagno);
        List<Alimentazione> alimentazioneList = alimentazioneRepository.findByTempo(
                idBagno,
                dataControllo.getDayOfWeek().name());
        AlimentazioneRisposta alimentazioneRisposta = AlimentazioneRisposta.builder()
                .idBagno(idBagno)
                .messaggio(tempoCalcolaSoloMessaggio(
                        dataControllo,
                        storicoGeneraleTempoLast,
                        alimentazioneList))
                .build();

        LocalDate dataUltimoStorico;

        if (storicoGeneraleTempoLast != null && dataControllo.isAfter(
                storicoGeneraleTempoLast.getDataControlloTempo().plusDays(10))) {
            throw new RuntimeException(
                    "imbecille! ci sono aggiunte a tempo da confermare " +
                            "o escludere da più di 10 giorni in archivio!");
        }
        if (storicoGeneraleTempoLast == null) {
            dataUltimoStorico = LocalDate.now();

            if (alimentazioneList.isEmpty()) {
                return alimentazioneRisposta;
            }
        } else {
            dataUltimoStorico = storicoGeneraleTempoLast.getDataControlloTempo();
        }

        if (!dataControllo.isAfter(dataUltimoStorico) || dataControllo.isAfter(
                LocalDate.now().plusDays(7))) {
            return alimentazioneRisposta;
        }

        for (LocalDate data = dataUltimoStorico.plusDays(1);
             !data.isAfter(dataControllo); data = data.plusDays(1)) {
            for (Alimentazione alimentazione : alimentazioneList) {

                if (alimentazione.getTempo().contains(data.getDayOfWeek())) {
                    tempoCreaStorico(alimentazione, data);
                }
            }
        }
        List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, false);
        alimentazioneRisposta.setOggettoAggiuntaList(aggiuntaDaStoriciPassatiList(
                storicoGeneraleDaEsegureList));
        return alimentazioneRisposta;
    }

    public String tempoCalcolaSoloMessaggio(
            LocalDate dataControllo,
            StoricoGenerale storicoGeneraleTempoLast,
            List<Alimentazione> alimentazioneList) {
        String stringRisposta;
        if (storicoGeneraleTempoLast == null || dataControllo.isAfter(
                storicoGeneraleTempoLast.getDataControlloTempo().plusDays(10))) {
            String rispostaPrimaPt;
            if (storicoGeneraleTempoLast == null) {
                rispostaPrimaPt = "In questo bagno non sono mai state eseguite aggiunte " +
                        "a tempo prima. le aggiunte cominceranno da oggi.";
                if (alimentazioneList.isEmpty()) {
                    stringRisposta = rispostaPrimaPt + " Non ci sono aggiunte da eseguire oggi.";
                    return stringRisposta;
                }
                stringRisposta = rispostaPrimaPt + " queste sono le aggiunte da eseguire di oggi.";
            } else {
                stringRisposta = "In questo bagno è stata eseguita l'ultima aggiunta a tempo " +
                        "da più di 10 giorni; il : "
                        + storicoGeneraleTempoLast.getDataControlloTempo()
                        + ". Gestire le vecchie aggiunte prima di ricominciare.";
            }
            return stringRisposta;


        }
        if (!dataControllo.isAfter(storicoGeneraleTempoLast.getDataControlloTempo()) || dataControllo.isAfter(
                LocalDate.now().plusDays(7))) {
            stringRisposta =
                    "la data inserta è precedente l'ultima aggiunta fatta il  "
                            + storicoGeneraleTempoLast.getDataControlloTempo() +
                            " Oppure la data di controllo inserita (" + dataControllo + ") è più di 7 giorni avanti alla data di oggi "
                            + LocalDate.now() + ". Non verranno calcolate aggiunte";

            return stringRisposta;
        }
        stringRisposta = "Queste sono le aggiunte non ancora eseguite da " +
                storicoGeneraleTempoLast.getDataControlloTempo() + " a " + dataControllo;
        return stringRisposta;
    }


    private void tempoCreaStorico(Alimentazione alimentazione,
                                  LocalDate dataControllo) {

        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(alimentazione.getBagno())
                        .sonoScatti(false)
                        .dataControlloTempo(dataControllo)
                        .build());
        for (DettaglioAlimentazione dettaglio : alimentazione
                .getDettaglioAlimentazioneList()) {

            storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(storicoGenerale)
                            .quantita(dettaglio.getQuantitaProdotto())
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build());
        }
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


    private StoricoGenerale trovaStoricoGenerale(long id) {
        Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(
                id);
        return storicoGeneraleTrovato.orElse(null);
    }

    private Bagno trovaBagno(long id) {
        Optional<Bagno> bagnoTrovato = bagnoRepository.findById(id);
        return bagnoTrovato.orElse(null);
    }


}
