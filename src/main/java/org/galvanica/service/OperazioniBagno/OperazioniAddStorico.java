package org.galvanica.service.OperazioniBagno;

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

    //todo: attenzione, quando si confermano le aggiunte storico precedenti vanno approvate in ordine crescente di data, mai al contrario o non tornano gli scatti totali e parziali
//todo: attenzione! in questo momentoo viene ricercato su StoricoGenerale tutte gli storici (sia con il parametro scatti che con il parametro tempo), da implementare controllo!
    public OperazioniAddStorico(BagnoRepository bagnoRepository,
                                StoricoGeneraleRepository storicoGeneraleRepository,
                                StoricoDettaglioRepository storicoDettaglioRepository,
                                AlimentazioneRepository alimentazioneRepository) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.alimentazioneRepository = alimentazioneRepository;
    }

    public AlimentazioneRisposta scattiCalcolaAlimentazione(Long id,
                                                            Integer scattiParziali) {
        Bagno bagno = trovaBagno(id);
        Alimentazione alimentazione = scattiTrovaAlimentazione(bagno);
        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, id, true);
        int scattiAttuali = scattiParziali;
        int scattiTotali;
        boolean risposta = !storicoGeneraleListDaEseguire.isEmpty();
        if (risposta) {
            scattiAttuali = storicoGeneraleListDaEseguire.getFirst()
                    .getRestoScatti() + scattiAttuali;
            scattiTotali = storicoGeneraleListDaEseguire.getFirst()
                    .getScattiTotali() + scattiParziali;
        } else {
            scattiAttuali = bagno.getRestoScatti() + scattiAttuali;
            scattiTotali = bagno.getScattiTotali() + scattiParziali;
        }
        Double primoValoreVolumetrico = trovaPrimoValoreVolumetrico(alimentazione);

        ScattiMath scattiMath = MetodiArrotondamenti.alimentazioneScattiMath(
                scattiAttuali,
                alimentazione.getScatti(),
                alimentazione.getArrotondaValori(),
                primoValoreVolumetrico);

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

        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(bagno)
                        .scattiTotali(scattiTotali)
                        .restoScatti((int) scattiMath.getRestoScatti())
                        .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                        .build());


        scattiConteggiProdotti(
                alimentazione,
                scattiMath,
                storicoGenerale);


        return AlimentazioneRisposta.builder()
                .idBagno(id)
                .oggettoAggiuntaList(aggiuntaDaStoriciPassatiList(
                        storicoGeneraleListDaEseguire))
                .restoScatti((int) scattiMath.getRestoScatti())
                .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                .build();
    }

    public AlimentazioneRisposta tempoCalcolaAlimentazione(
            Long idBagno, LocalDate dataControllo) {
        StoricoGenerale storicoGeneraletempoLast = storicoGeneraleRepository.storicoGeneraleTempoLast(
                idBagno);
        String findByTempo = dataControllo.getDayOfWeek().name();
        List<Alimentazione> alimentazioneList = alimentazioneRepository.findByTempo(
                idBagno,
                findByTempo);
        AlimentazioneRisposta alimentazioneRisposta = AlimentazioneRisposta.builder()
                .idBagno(idBagno)
                .build();
        if (storicoGeneraletempoLast == null || dataControllo.isAfter(
                storicoGeneraletempoLast.getDataControlloTempo().plusDays(10))) {
            String rispostaPrimaPt = null;
            if (storicoGeneraletempoLast == null) {
                rispostaPrimaPt = "In questo bagno non sono mai state eseguite aggiunte a tempo prima. le aggiunte cominceranno da oggi.";
            } else {
                rispostaPrimaPt = "In questo bagno è stata eseguita l'ultima aggiunta a tempo da più di 10 giorni; il : "
                        + storicoGeneraletempoLast.getDataControlloTempo() + ". le aggiunte ricominceranno da oggi.";
            }
            if (alimentazioneList.isEmpty()) {
                alimentazioneRisposta.setMessaggio(rispostaPrimaPt + " Non ci sono aggiunte da eseguire oggi.");
                return alimentazioneRisposta;
            }
            for (Alimentazione alimentazione : alimentazioneList) {
                tempoCreaStoricoDettaglio(tempoCreaStoricoGenerale(alimentazione,
                        dataControllo));
            }
            List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleDescList(
                    false, idBagno, false);
            alimentazioneRisposta.setOggettoAggiuntaList(
                    aggiuntaDaStoriciPassatiList(storicoGeneraleDaEsegureList));
            alimentazioneRisposta.setMessaggio(rispostaPrimaPt + " queste sono le aggiunte da eseguire di oggi.");
            return alimentazioneRisposta;
        }
        if (!dataControllo.isAfter(storicoGeneraletempoLast.getDataControlloTempo()) || dataControllo.isAfter(
                LocalDate.now().plusDays(7))) {
            alimentazioneRisposta.setMessaggio(
                    "la data inserta è precedente l'ultima aggiunta fatta il  "
                            + storicoGeneraletempoLast.getDataControlloTempo() +
                            " Oppure la data di controllo inserita (" + dataControllo + ") è più di 7 giorni avanti alla data di oggi "
                            + LocalDate.now() + ". Non verranno fatte aggiunte");

            return alimentazioneRisposta;
        }
        for (LocalDate data = storicoGeneraletempoLast.getDataControlloTempo()
                .plusDays(1);
             !data.isAfter(dataControllo); data = data.plusDays(1)) {
            for (Alimentazione alimentazione : alimentazioneList) {
                if (alimentazione.getTempo().contains(data.getDayOfWeek().name())) {
                    tempoCreaStoricoDettaglio(tempoCreaStoricoGenerale(alimentazione,
                            dataControllo));
                }
            }
        }
        List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, false);
        alimentazioneRisposta.setOggettoAggiuntaList(
                aggiuntaDaStoriciPassatiList(storicoGeneraleDaEsegureList));
        alimentazioneRisposta.setMessaggio(
                "Queste sono le aggiunte non ancora eseguite da " + storicoGeneraletempoLast.getDataControlloTempo() + " a " + dataControllo);
        return alimentazioneRisposta;
    }


    private List<StoricoDettaglio> scattiConteggiProdotti(
            Alimentazione alimentazione,
            ScattiMath scattiMath,
            StoricoGenerale storicoGenerale) {
        List<StoricoDettaglio> storicoDettaglioList = new ArrayList<>();
        StoricoGenerale storico = trovaStoricoGenerale(storicoGenerale.getIdStorico());
        for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {
            double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto() * scattiMath.getMoltiplicatoreAlimentazione();
            if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
                quantitaProdottoAggiunta = MetodiArrotondamenti.approssimazioneAggiunta(
                        quantitaProdottoAggiunta);
            }
            StoricoDettaglio storicoDettaglio = storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(storico)
                            .quantita(quantitaProdottoAggiunta)
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build());

            storicoDettaglioList.add(storicoDettaglio);
        }
        return storicoDettaglioList;

    }

    private Alimentazione scattiTrovaAlimentazione(Bagno bagno) {
        return bagno.getAlimentazioneList()
                .stream()
                .filter(alimentazioneFilter -> alimentazioneFilter.getScatti() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Alimentazione a scatti non trovata per bagno " + bagno.getIdBagno() + " nome: " + bagno.getNome()));
    }


    private StoricoGenerale tempoCreaStoricoGenerale(Alimentazione alimentazione,
                                                     LocalDate dataControllo) {

        return storicoGeneraleRepository.save(StoricoGenerale.builder()
                .alimentazione(alimentazione)
                .bagno(alimentazione.getBagno())
                .sonoScatti(false)
                .dataControlloTempo(dataControllo)
                .build());
    }

    private List<StoricoDettaglio> tempoCreaStoricoDettaglio(
            StoricoGenerale storicoGenerale) {
        List<StoricoDettaglio> storicoDettaglioList = new ArrayList<>();
        for (DettaglioAlimentazione dettaglio : storicoGenerale.getAlimentazione()
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


    private List<OggettoAggiunta> aggiuntaDaStoriciPassatiList(
            List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        Map<Long, OggettoAggiunta> oggettoAggiuntaMap = new HashMap<>();
        List<StoricoDettaglio> storicoDettaglioList = storicoGeneraleListDaEseguire.stream()
                .flatMap(storicoGenerale -> storicoGenerale.getStoricoDettaglioList()
                        .stream())
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
