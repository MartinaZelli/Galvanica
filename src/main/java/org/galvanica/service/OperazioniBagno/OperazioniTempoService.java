package org.galvanica.service.OperazioniBagno;

import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.OggettoAggiunta;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.DettaglioAlimentazione;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperazioniTempoService {

    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final AlimentazioneRepository alimentazioneRepository;

    //todo: attenzione, quando si confermano le aggiunte storico precedenti vanno approvate in ordine crescente di data, mai al contrario o non tornano gli scatti totali e parziali
//todo: attenzione! in questo momentoo viene ricercato su StoricoGenerale tutte gli storici (sia con il parametro scatti che con il parametro tempo), da implementare controllo!
    public OperazioniTempoService(BagnoRepository bagnoRepository,
                                  StoricoGeneraleRepository storicoGeneraleRepository,
                                  StoricoDettaglioRepository storicoDettaglioRepository,
                                  AlimentazioneRepository alimentazioneRepository) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.alimentazioneRepository = alimentazioneRepository;
    }

    private AlimentazioneRisposta calcolaAlimentazioneTempo(
            LocalDate dataControllo, Long idBagno) {
        StoricoGenerale storicoGeneraletempoLast = storicoGeneraleRepository.storicoGeneraleTempoLast(
                idBagno);
        List<Alimentazione> alimentazioneList = alimentazioneRepository.findByTempo(
                dataControllo.getDayOfWeek().name());
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
                creaStoricoDettaglioTempo(creaStoricoGeneraleTempo(alimentazione,
                        dataControllo));
            }
            List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleTempoList(
                    false, idBagno);
            alimentazioneRisposta.setOggettoAggiuntaList(
                    conteggiAlimentazioneDaStoricoTempo(storicoGeneraleDaEsegureList));
            alimentazioneRisposta.setMessaggio(rispostaPrimaPt + " queste sono le aggiunte da eseguire di oggi.");
            return alimentazioneRisposta;
        }
        if (!dataControllo.isAfter(storicoGeneraletempoLast.getDataControlloTempo()) || !dataControllo.isAfter(
                LocalDate.now().plusDays(7))) {
            alimentazioneRisposta.setMessaggio(
                    "la data inserta è precedente l'ultima aggiunta fatta il  "
                            + storicoGeneraletempoLast.getDataControlloTempo() +
                            " Oppure la data di controllo inserita (" + dataControllo + ") è più di 10 giorni avanti alla data di oggi"
                            + LocalDate.now() + ". Non verranno fatte aggiunte");
            return alimentazioneRisposta;
        }
        for (LocalDate data = storicoGeneraletempoLast.getDataControlloTempo()
                .plusDays(1);
             !data.isAfter(dataControllo); data = data.plusDays(1)) {
            for (Alimentazione alimentazione : alimentazioneList) {
                if (alimentazione.getTempo().contains(data.getDayOfWeek().name())) {
                    creaStoricoDettaglioTempo(creaStoricoGeneraleTempo(alimentazione,
                            dataControllo));
                }
            }
        }
        List<StoricoGenerale> storicoGeneraleDaEsegureList = storicoGeneraleRepository.storicoGeneraleTempoList(
                false, idBagno);
        alimentazioneRisposta.setOggettoAggiuntaList(
                conteggiAlimentazioneDaStoricoTempo(storicoGeneraleDaEsegureList));
        alimentazioneRisposta.setMessaggio(
                "Queste sono le aggiunte non ancora eseguite da " + storicoGeneraletempoLast.getDataControlloTempo() + " a " + dataControllo);
        return null;
    }

    private StoricoGenerale creaStoricoGeneraleTempo(Alimentazione alimentazione,
                                                     LocalDate dataControllo) {

        return storicoGeneraleRepository.save(StoricoGenerale.builder()
                .alimentazione(alimentazione)
                .bagno(alimentazione.getBagno())
                .sonoScatti(false)
                .dataControlloTempo(dataControllo)
                .build());
    }

    private List<StoricoDettaglio> creaStoricoDettaglioTempo(
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

    //todo: uguale a aggiuntadaStoricipassatiList
    private List<OggettoAggiunta> conteggiAlimentazioneDaStoricoTempo(
            List<StoricoGenerale> storicoGeneraleListDaEseguire) {
        List<OggettoAggiunta> oggettoAggiuntaList = new ArrayList<>();
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

    //todo: uguale a oggettoAggiuntaTrasformer
    private OggettoAggiunta oggettoAggiuntaTrasformer(StoricoDettaglio dettaglio) {
        return OggettoAggiunta.builder()
                .unitaDiMisura(dettaglio.getUnitaDiMisura())
                .quantitaProdotto(dettaglio.getQuantita())
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .build();
    }


}
