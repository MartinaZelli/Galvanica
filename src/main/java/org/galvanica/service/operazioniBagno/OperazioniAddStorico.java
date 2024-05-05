package org.galvanica.service.operazioniBagno;

import jakarta.persistence.EntityManager;
import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.OggettoAggiunta;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.ScattiMath;
import org.galvanica.model.*;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class OperazioniAddStorico {

    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final AlimentazioneRepository alimentazioneRepository;

    private final EntityManager entityManager;

    //todo: attenzione, quando si confermano le aggiunte storico precedenti vanno approvate in ordine crescente di data, mai al contrario o non tornano gli scatti totali e parziali
//todo: attenzione! in questo momentoo viene ricercato su StoricoGenerale tutte gli storici (sia con il parametro scatti che con il parametro tempo), da implementare controllo!
    public OperazioniAddStorico(BagnoRepository bagnoRepository,
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
        for (Long id : idBagnoScattiParzialiMap.keySet()) {
            risposta.add(scattiCalcolaAlimentazione(id, id.intValue()));
        }
        return risposta;
    }

    public AlimentazioneRisposta scattiCalcolaAlimentazione(Long id,
                                                            Integer scattiParziali) {
        //trova bagno, alimentazione e storico
        Bagno bagno = trovaBagno(id);
        Alimentazione alimentazione = scattiTrovaAlimentazione(bagno);
        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, id, true);
        //gestione scattiAttuali e scatti parziali
        int scattiAttuali = scattiParziali;
        int scattiTotali;
        boolean risposta = !storicoGeneraleListDaEseguire.isEmpty();
        //se listastorico è popolata: aggiungi a scatti precedenti quelli della chiamata.
        if (risposta) {
            scattiAttuali = storicoGeneraleListDaEseguire.getFirst().getRestoScatti()
                    + scattiAttuali;
            scattiTotali = storicoGeneraleListDaEseguire.getFirst().getScattiTotali()
                    + scattiParziali;
            //altrimenti prendi scatti dal bagno (già aggiornato)
        } else {
            scattiAttuali = bagno.getRestoScatti() + scattiAttuali;
            scattiTotali = bagno.getScattiTotali() + scattiParziali;
        }

        //conti: trova valore volumetrico ; valore null gestito in MetodiArrotondamenti.alimentazioneScattiMath
        Double primoValoreVolumetrico = trovaPrimoValoreVolumetrico(alimentazione);

        //trova scattiMath: private long restoScatti;
        //                  private double moltiplicatoreAlimentazione;
        ScattiMath scattiMath = MetodiArrotondamenti.alimentazioneScattiMath(
                scattiAttuali,
                alimentazione.getScatti(),
                alimentazione.getArrotondaValori(),
                primoValoreVolumetrico);

        //se non c'è necessità di alimentazioni allora setta nuovo scattiTotali e scattiAttuali
        // su bagno o repository se presente
        if (scattiMath.getMoltiplicatoreAlimentazione() == 0) {
            System.out.println(
                    "il bagno non richiede aggiunte per ora. nuovo resto scatti : " + scattiAttuali);
            if (risposta) {
                storicoGeneraleListDaEseguire.getFirst()
                        .setScattiTotali(scattiTotali);
                storicoGeneraleListDaEseguire.getFirst()
                        .setRestoScatti(scattiAttuali);
                storicoGeneraleRepository.save(storicoGeneraleListDaEseguire.getLast());
            } else {
                bagno.setScattiTotali(scattiTotali);
                bagno.setRestoScatti(scattiAttuali);
                bagnoRepository.save(bagno);
            }

            return AlimentazioneRisposta.builder()
                    .idBagno(id)
                    .restoScatti(scattiAttuali)
                    .messaggio("gli scatti sono inferiori al 90% dell'alimentazione,"
                            + "le aggiunte non verranno eseguite ma messe in conto per la prossima chiamata.")
                    .moltiplicatoreAlimentazione(0D)
                    .build();
        }
        //altrimenti (se c'è alimentazione).
        // A: crea nuovo storicoGenerale
        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(bagno)
                        .scattiTotali(scattiTotali)
                        .restoScatti((int) scattiMath.getRestoScatti())
                        .sonoScatti(true)
                        .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                        .build());

        // B: crea gli storicoDettaglio per ogni dettaglioAlimentazione trovato.
        scattiConteggiProdotti(
                alimentazione,
                scattiMath,
                storicoGenerale);
        storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, id, true);

        return AlimentazioneRisposta.builder()
                .idBagno(id)
                //TODO: verificare che basti in AlimentazioneRisposta aggiuntaDaStoriciPassatiList
                // e non debba essere implementata scattiConteggiProdotti.
                .oggettoAggiuntaList(aggiuntaDaStoriciPassatiList(
                        storicoGeneraleListDaEseguire))
                .restoScatti((int) scattiMath.getRestoScatti())
                .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                .build();
    }

    //crea storico dettaglio con approssimazioni di misura per volume
    private List<StoricoDettaglio> scattiConteggiProdotti(
            Alimentazione alimentazione,
            ScattiMath scattiMath,
            StoricoGenerale storicoGenerale) {
        List<StoricoDettaglio> storicoDettaglioList = new ArrayList<>();
        for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {
            double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto()
                    * scattiMath.getMoltiplicatoreAlimentazione();
            if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
                quantitaProdottoAggiunta = MetodiArrotondamenti.moltiplicatoreApprossimatoPerAggiunta(
                        quantitaProdottoAggiunta);
            }
            StoricoDettaglio storicoDettaglio = storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(trovaStoricoGenerale(storicoGenerale.getIdStorico()))
                            .quantita(quantitaProdottoAggiunta)
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build());

            storicoDettaglioList.add(storicoDettaglio);
        }
        return storicoDettaglioList;

    }

    private List<OggettoAggiunta> aggiuntaDaStoriciPassatiList(
            List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        Map<Long, OggettoAggiunta> oggettoAggiuntaMap = new HashMap<>();

        List<StoricoDettaglio> storicoDettaglioList = storicoGeneraleListDaEseguire.stream()
                .map(s -> {
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
        //trova ultimo storicoGenerale per il Tempo per idbagno,
        // trova giorno settimana dataControllo,
        //trova lista delle alimentazioni da fare per il giorno della data di controllo
        //crea un' alimentazioneRisposta e la builda con idBagno.
        StoricoGenerale storicoGeneraleTempoLast =
                storicoGeneraleRepository.storicoGeneraleTempoLast(idBagno);
        String findByTempo = dataControllo.getDayOfWeek().name();
        //todo: controllare se ha senso alimentazioneRepository.findByTempo
        List<Alimentazione> alimentazioneList = alimentazioneRepository.findByTempo(
                idBagno,
                findByTempo);
        AlimentazioneRisposta alimentazioneRisposta = AlimentazioneRisposta.builder()
                .idBagno(idBagno)
                .build();

        //se non c'è uno storicoGeneraleTempo o la data di controllo
        // è successiva all'ultimo storicoGeneraleTempo di 10 giorni o più
        // il conteggio riparte da oggi.
        //todo: se data controllo+10giorni allora dovremmo segnare come annullate le aggiunte precedenti!
        if (storicoGeneraleTempoLast == null || dataControllo.isAfter(
                storicoGeneraleTempoLast.getDataControlloTempo().plusDays(10))) {
            String rispostaPrimaPt = null;
            if (storicoGeneraleTempoLast == null) {
                rispostaPrimaPt = "In questo bagno non sono mai state eseguite aggiunte a tempo prima. le aggiunte cominceranno da oggi.";
            } else {
                rispostaPrimaPt = "In questo bagno è stata eseguita l'ultima aggiunta a tempo da più di 10 giorni; il : "
                        + storicoGeneraleTempoLast.getDataControlloTempo() + ". le aggiunte ricominceranno da oggi.";
            }
            //se non ci sono alimentazioni da fare ritorna alimentazioneRisposta.
            if (alimentazioneList.isEmpty()) {
                alimentazioneRisposta.setMessaggio(rispostaPrimaPt + " Non ci sono aggiunte da eseguire oggi.");
                return alimentazioneRisposta;
            }

            //altrimenti si ciclano le alimentazioni da lista e usiamo tempoCreaStorico
            // per creare storico generale e dettaglio di ogni alimentazione.
            for (Alimentazione alimentazione : alimentazioneList) {
                tempoCreaStorico(alimentazione, dataControllo);
            }
            //crea una lista di tutte le aggiunte a tempo non fatte per il bagno
            //todo: in questo caso potremmo metterle direttamente nel ciclo for precedente??
            List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleDescList(
                    false, idBagno, false);

            //todo: arrivata fino a qui
            System.out.println(storicoGeneraleDaEsegureList.getFirst()
                    .getStoricoDettaglioList());
            alimentazioneRisposta.setOggettoAggiuntaList(aggiuntaDaStoriciPassatiList(
                    storicoGeneraleDaEsegureList));
            alimentazioneRisposta.setMessaggio(rispostaPrimaPt + " queste sono le aggiunte da eseguire di oggi.");
            return alimentazioneRisposta;
        }
        if (!dataControllo.isAfter(storicoGeneraleTempoLast.getDataControlloTempo()) || dataControllo.isAfter(
                LocalDate.now().plusDays(7))) {
            alimentazioneRisposta.setMessaggio(
                    "la data inserta è precedente l'ultima aggiunta fatta il  "
                            + storicoGeneraleTempoLast.getDataControlloTempo() +
                            " Oppure la data di controllo inserita (" + dataControllo + ") è più di 7 giorni avanti alla data di oggi "
                            + LocalDate.now() + ". Non verranno fatte aggiunte");

            return alimentazioneRisposta;
        }
        for (LocalDate data = storicoGeneraleTempoLast.getDataControlloTempo()
                .plusDays(1);
             !data.isAfter(dataControllo); data = data.plusDays(1)) {
            for (Alimentazione alimentazione : alimentazioneList) {
                if (alimentazione.getTempo().contains(data.getDayOfWeek().name())) {
                    tempoCreaStorico(alimentazione, dataControllo);
                }
            }
        }
        List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, false);
        alimentazioneRisposta.setOggettoAggiuntaList(aggiuntaDaStoriciPassatiList(
                storicoGeneraleDaEsegureList));
        alimentazioneRisposta.setMessaggio(
                "Queste sono le aggiunte non ancora eseguite da " + storicoGeneraleTempoLast.getDataControlloTempo() + " a " + dataControllo);
        return alimentazioneRisposta;
    }


    private List<StoricoDettaglio> tempoCreaStorico(Alimentazione alimentazione,
                                                    LocalDate dataControllo) {

        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(alimentazione.getBagno())
                        .sonoScatti(false)
                        .dataControlloTempo(dataControllo)
                        .build());
        List<StoricoDettaglio> storicoDettaglioList = new ArrayList<>();
        for (DettaglioAlimentazione dettaglio : alimentazione
                .getDettaglioAlimentazioneList()) {

            storicoDettaglioList.add(storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(storicoGenerale)
                            .quantita(dettaglio.getQuantitaProdotto())
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build()));
        }
        return storicoDettaglioList;

    }


    private OggettoAggiunta oggettoAggiuntaTrasformer(StoricoDettaglio dettaglio) {
        return OggettoAggiunta.builder()
                .unitaDiMisura(dettaglio.getUnitaDiMisura())
                .quantitaProdotto(dettaglio.getQuantita())
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .build();
    }

    private Double trovaPrimoValoreVolumetrico(Alimentazione alimentazione) {
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
        return storicoGeneraleTrovato.orElseThrow(() -> new RuntimeException(
                "storico non trovato per id " + id));
    }

    private Bagno trovaBagno(long id) {
        Optional<Bagno> bagnoTrovato = bagnoRepository.findById(id);
        return bagnoTrovato.orElseThrow(() -> new RuntimeException(
                "bagno non trovato per id " + id));
    }


}
