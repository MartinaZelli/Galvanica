package org.galvanica.service.operazioniBagno;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.*;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.galvanica.math.ConvertitoreUnitaMisura.convertiQuantitaGenerico;
import static org.galvanica.math.ConvertitoreUnitaMisura.convertiUnitaMisuraPerDto;

@Service
public class AlimentazioneATempoService {


    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final AlimentazioneRepository alimentazioneRepository;
    private final BagnoService bagnoService;


//todo: impostare i controlli a monte di tutti i metodi

    //todo: attenzione, quando si confermano le aggiunte storico precedenti vanno approvate in ordine crescente di data, mai al contrario o non tornano gli scatti totali e parziali
//todo: attenzione! in questo momentoo viene ricercato su StoricoGenerale tutte gli storici (sia con il parametro scatti che con il parametro tempo), da implementare controllo!
    public AlimentazioneATempoService(
            StoricoGeneraleRepository storicoGeneraleRepository,
            StoricoDettaglioRepository storicoDettaglioRepository,
            AlimentazioneRepository alimentazioneRepository,
            BagnoService bagnoService) {

        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.alimentazioneRepository = alimentazioneRepository;

        this.bagnoService = bagnoService;
    }

    public void calcolaAlimentazione(
            Long idBagno, LocalDate dataControllo) {
        calcolaAlimentazioneControlliApprovati(idBagno);
        StoricoGenerale storicoGeneraleTempoLast =
                storicoGeneraleRepository.storicoGeneraleTempoLast(idBagno);
        LocalDate dataUltimoStorico = dataControllo;
        if (storicoGeneraleTempoLast != null) {
            dataUltimoStorico = storicoGeneraleTempoLast.getDataControlloTempo();
        }
        if (dataControllo.plusDays(1).isBefore(dataUltimoStorico)) {
            throw new RuntimeException("le alimentazioni fino alla data " + dataControllo + " sono già state eseguite." +
                    "riprendere da dopo la data " + dataUltimoStorico);
        }

        Stream<LocalDate> date =
                Stream.iterate(dataUltimoStorico, data -> data.plusDays(1))
                        .limit(dataControllo.plusDays(1)
                                .compareTo(dataUltimoStorico));
        date.forEach(data -> {
            List<Alimentazione> alimentazioneList = alimentazioneRepository.findByTempo(
                    idBagno,
                    data.getDayOfWeek().name());
            if (alimentazioneList == null || alimentazioneList.isEmpty()) {
                return;
            }
            for (Alimentazione alimentazione : alimentazioneList) {
                creaStorico(alimentazione, data);
            }
        });
    }

    private void creaStorico(Alimentazione alimentazione,
                             LocalDate dataControllo) {

        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(alimentazione.getBagno())
                        .tipologiaAggiunta(TipologiaAggiunta.TEMPO)
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

    public List<StoricoTotaleSingoloDto> calcolaAggiunteStoricoPerIdBagnoDaDataAData(
            Long idBagno, LocalDate dataFrom, LocalDate dataTo) {
        calcolaAlimentazioneControlliApprovati(idBagno);

        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, TipologiaAggiunta.TEMPO);

        List<StoricoDettaglio> storicoDettaglioList = storicoGeneraleListDaEseguire.stream()
                .filter(storicoGenerale -> storicoGenerale.getStoricoDettaglioList() != null &&
                        !storicoGenerale.getStoricoDettaglioList().isEmpty())
                .filter(s -> s.getDataControlloTempo()
                        .isAfter(dataFrom.minusDays(1)) &&
                        s.getDataControlloTempo().isBefore(dataTo.plusDays(1)))
                .map(StoricoGenerale::getStoricoDettaglioList)
                .flatMap(Collection::parallelStream)
                .filter(storicoDettaglio -> !storicoDettaglio.getEseguitoDettaglio() &&
                        !storicoDettaglio.getAnnullatoDettaglio())
                .toList();
        List<StoricoTotaleSingoloDto> storicoTotaleSingoloDtoList = new ArrayList<>();
        for (StoricoDettaglio dettaglio : storicoDettaglioList) {
            storicoTotaleSingoloDtoList.add(buildPerSingolo(dettaglio));
        }
        return storicoTotaleSingoloDtoList;
    }

    private StoricoTotaleSingoloDto buildPerSingolo(
            StoricoDettaglio dettaglio) {
        UnitaDiMisura unitaDiMisura = convertiUnitaMisuraPerDto(
                dettaglio.getQuantita(),
                dettaglio.getUnitaDiMisura().isSonoVolume());
        Double quantita = convertiQuantitaGenerico(
                Double.valueOf(dettaglio.getQuantita()),
                dettaglio.getUnitaDiMisura(),
                unitaDiMisura);
        return StoricoTotaleSingoloDto.builder()
                .idStoricoDettaglio(dettaglio.getIdStoricoDettaglio())
                .idStoricoGenerale(dettaglio.getStoricoGenerale().getIdStorico())
                .idBagno(dettaglio.getStoricoGenerale().getBagno().getIdBagno())
                .nomeBagno(dettaglio.getStoricoGenerale().getBagno().getNome())
                .quantitaProdotto(quantita)
                .unitaDiMisura(unitaDiMisura)
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .nomeProdotto(dettaglio.getProdotto().getNome())
                .idAlimentazione(dettaglio.getStoricoGenerale()
                        .getAlimentazione()
                        .getIdAlimentazione())
                .dataControlloTempo(dettaglio.getStoricoGenerale()
                        .getDataControlloTempo())
                .tipologiaAggiunta(dettaglio.getStoricoGenerale()
                        .getTipologiaAggiunta())
                .noteStoricoGenerale(dettaglio.getStoricoGenerale().getNote())
                .dataCreazione(dettaglio.getStoricoGenerale().getDataCreazione())
                .build();
    }

    public List<StoricoTotaleGroupDto> calcolaAggiunteStoricoPerIdBagno(
            Long idBagno) {
        calcolaAlimentazioneControlliApprovati(idBagno);

        Map<Long, StoricoTotaleGroupDto> mappa = new HashMap<>();
        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository.storicoGeneraleDescList(
                false, idBagno, TipologiaAggiunta.TEMPO);

        List<StoricoDettaglio> storicoDettaglioList = storicoGeneraleListDaEseguire.stream()
                .filter(storicoGenerale -> storicoGenerale.getStoricoDettaglioList() != null &&
                        !storicoGenerale.getStoricoDettaglioList().isEmpty())
                .map(StoricoGenerale::getStoricoDettaglioList)
                .flatMap(Collection::parallelStream)
                .filter(storicoDettaglio -> !storicoDettaglio.getEseguitoDettaglio() &&
                        !storicoDettaglio.getAnnullatoDettaglio())
                .toList();


        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            if (mappa.containsKey(storicoDettaglio.getProdotto()
                    .getIdProdotto())) {
                Long key = storicoDettaglio.getProdotto().getIdProdotto();
                Double quantitaProdotto = mappa.get(key)
                        .getQuantitaProdotto() + storicoDettaglio.getQuantita();

                mappa.get(key).setQuantitaProdotto(quantitaProdotto);

                mappa.get(key)
                        .getIdStoricoDettaglioList()
                        .add(storicoDettaglio.getIdStoricoDettaglio());
            }
            if (!mappa.containsKey(storicoDettaglio.getProdotto()
                    .getIdProdotto())) {
                mappa.put(storicoDettaglio.getProdotto()
                        .getIdProdotto(), buildPerGroup(storicoDettaglio));
            }
        }
        for (StoricoTotaleGroupDto storico : mappa.values()) {

            UnitaDiMisura unitaDiMisura = convertiUnitaMisuraPerDto(
                    (int) Math.round(storico.getQuantitaProdotto()),
                    storico.getUnitaDiMisura().isSonoVolume());
            Double quantita = convertiQuantitaGenerico(
                    storico.getQuantitaProdotto(),
                    storico.getUnitaDiMisura(),
                    unitaDiMisura);
            mappa.get(storico.getIdProdotto()).setQuantitaProdotto(quantita);
            mappa.get(storico.getIdProdotto()).setUnitaDiMisura(unitaDiMisura);

            String messaggio = "Queste sono tutte le aggiunte non ancora effettuate per il bagno.";
            mappa.get(storico.getIdProdotto()).setRispostaCalcoloFront(messaggio);
        }

        return new ArrayList<>(mappa.values());
    }

    private StoricoTotaleGroupDto buildPerGroup(
            StoricoDettaglio dettaglio) {
        List<Long> idStoricoDettaglioList = new ArrayList<>();
        idStoricoDettaglioList.add(dettaglio.getIdStoricoDettaglio());

        return StoricoTotaleGroupDto.builder()
                .idBagno(dettaglio.getStoricoGenerale().getBagno().getIdBagno())
                .nomeBagno(dettaglio.getStoricoGenerale().getBagno().getNome())
                .idAlimentazione(dettaglio.getStoricoGenerale().getAlimentazione()
                        .getIdAlimentazione())
                .tipologiaAggiunta(dettaglio.getStoricoGenerale()
                        .getTipologiaAggiunta())
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .nomeProdotto(dettaglio.getProdotto().getNome())
                .quantitaProdotto((double) dettaglio.getQuantita())
                .unitaDiMisura(dettaglio.getUnitaDiMisura())
                .idStoricoDettaglioList(idStoricoDettaglioList)
                .build();
    }

    private void calcolaAlimentazioneControlliApprovati(Long idBagno) {
        //todo:impostare i controlli a monte di tutti i metodi
        Bagno bagno = bagnoService.modelRicercaId(idBagno);
        if (bagno.getAlimentazioneList()
                .stream()
                .noneMatch(alimentazione -> alimentazione.getTempo() == null || alimentazione.getTempo()
                        .isEmpty())) {
            throw new RuntimeException(
                    "Alimentazione a scatti non trovata per bagno " + idBagno);
        }
    }


}
