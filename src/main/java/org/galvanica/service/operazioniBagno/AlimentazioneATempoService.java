package org.galvanica.service.operazioniBagno;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.dto.risposta.RispostaDettaglio;
import org.galvanica.dto.risposta.tempo.RispostaTempoGenerale;
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

    public List<RispostaTempoGenerale> calcolaRispostaList(List<Long> idBagnoList,
                                                           LocalDate dataControllo) {
        List<RispostaTempoGenerale> rispostaTempoGeneraleList = new ArrayList<>();
        for (Long id : idBagnoList) {
            Bagno bagno = bagnoService.modelRicercaId(id);
            if (bagno.getAlimentazioneList().stream().noneMatch(
                    alimentazione -> alimentazione.getTempo() == null ||
                            alimentazione.getTempo().isEmpty())) {
                continue;
            }
            rispostaTempoGeneraleList.add(creaRisposta(id, dataControllo));
        }
        return rispostaTempoGeneraleList;
    }

    public RispostaTempoGenerale creaRisposta(Long idBagno,
                                              LocalDate dataControllo) {
        List<Long> listId = calcolaAlimentazione(idBagno, dataControllo);
        StoricoGenerale ultimoStorico = storicoGeneraleRepository.storicoATempoNonInLista(
                idBagno,
                listId);

        return rispostaTempoBuilder(dataControllo,
                ultimoStorico.getDataControlloTempo(),
                listId);
    }

    public List<Long> calcolaAlimentazione(Long idBagno, LocalDate dataControllo) {
        calcolaAlimentazioneControlliApprovati(idBagno);
        LocalDate dataUltimoStorico = dataControllo;
        StoricoGenerale
                storicoGeneraleTempoLast =
                storicoGeneraleRepository.storicoGeneraleTempoLast(idBagno);

        if (storicoGeneraleTempoLast != null) {
            dataUltimoStorico = storicoGeneraleTempoLast.getDataControlloTempo();
        }
        if (dataControllo.plusDays(1).isBefore(dataUltimoStorico)) {
            throw new RuntimeException("le alimentazioni fino alla data " +
                    dataControllo +
                    " sono già state eseguite." +
                    "riprendere da dopo la data " +
                    dataUltimoStorico);
        }
        List<Long> idList = new ArrayList<>();
        Stream<LocalDate>
                date =
                Stream.iterate(dataUltimoStorico, data -> data.plusDays(1))
                        .limit(dataControllo.plusDays(1)
                                .compareTo(dataUltimoStorico));
        date.forEach(data -> {
            List<Alimentazione>
                    alimentazioneList =
                    alimentazioneRepository.findByTempo(idBagno,
                            data.getDayOfWeek().name());
            if (alimentazioneList == null || alimentazioneList.isEmpty()) {
                return;
            }
            for (Alimentazione alimentazione : alimentazioneList) {
                Long id = creaStorico(alimentazione, data);
                idList.add(id);
            }
        });
        return idList;
    }

    private Long creaStorico(Alimentazione alimentazione, LocalDate dataControllo) {

        StoricoGenerale
                storicoGenerale =
                storicoGeneraleRepository.save(StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(alimentazione.getBagno())
                        .tipologiaAggiunta(TipologiaAggiunta.TEMPO)
                        .dataControlloTempo(dataControllo)
                        .build());
        for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {

            storicoDettaglioRepository.save(
                    StoricoDettaglio.builder()
                            .prodotto(dettaglio.getProdotto())
                            .storicoGenerale(storicoGenerale)
                            .quantita(dettaglio.getQuantitaProdotto())
                            .unitaDiMisura(dettaglio.getUnitaDiMisura())
                            .build());
        }
        return storicoGenerale.getIdStorico();
    }

    public List<StoricoTotaleSingoloDto> calcolaAggiunteStoricoPerIdBagnoDaDataAData(
            Long idBagno,
            LocalDate dataFrom, LocalDate dataTo) {
        calcolaAlimentazioneControlliApprovati(idBagno);

        List<StoricoGenerale>
                storicoGeneraleListDaEseguire =
                storicoGeneraleRepository.storicoGeneraleDescList(false, idBagno,
                        TipologiaAggiunta.TEMPO);

        List<StoricoDettaglio>
                storicoDettaglioList =
                storicoGeneraleListDaEseguire.stream()
                        .filter(
                                storicoGenerale -> storicoGenerale.getStoricoDettaglioList() != null &&
                                        !storicoGenerale.getStoricoDettaglioList()
                                                .isEmpty())
                        .filter(
                                s -> s.getDataControlloTempo()
                                        .isAfter(dataFrom.minusDays(1)) &&
                                        s.getDataControlloTempo()
                                                .isBefore(dataTo.plusDays(1)))
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

    private StoricoTotaleSingoloDto buildPerSingolo(StoricoDettaglio dettaglio) {
        UnitaDiMisura
                unitaDiMisura =
                convertiUnitaMisuraPerDto(dettaglio.getQuantita(),
                        dettaglio.getUnitaDiMisura().isSonoVolume());
        Double
                quantita =
                convertiQuantitaGenerico(Double.valueOf(dettaglio.getQuantita()),
                        dettaglio.getUnitaDiMisura(), unitaDiMisura);
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
        List<StoricoGenerale>
                storicoGeneraleListDaEseguire =
                storicoGeneraleRepository.storicoGeneraleDescList(false, idBagno,
                        TipologiaAggiunta.TEMPO);

        List<StoricoDettaglio>
                storicoDettaglioList =
                storicoGeneraleListDaEseguire.stream()
                        .filter(
                                storicoGenerale -> storicoGenerale.getStoricoDettaglioList() != null &&
                                        !storicoGenerale.getStoricoDettaglioList()
                                                .isEmpty())
                        .map(StoricoGenerale::getStoricoDettaglioList)
                        .flatMap(Collection::parallelStream)
                        .filter(storicoDettaglio -> !storicoDettaglio.getEseguitoDettaglio() &&
                                !storicoDettaglio.getAnnullatoDettaglio())
                        .toList();

        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            if (mappa.containsKey(storicoDettaglio.getProdotto().getIdProdotto())) {
                Long key = storicoDettaglio.getProdotto().getIdProdotto();
                Double
                        quantitaProdotto =
                        mappa.get(key)
                                .getQuantitaProdotto() + storicoDettaglio.getQuantita();

                mappa.get(key).setQuantitaProdotto(quantitaProdotto);

                mappa.get(key).getIdStoricoDettaglioList()
                        .add(storicoDettaglio.getIdStoricoDettaglio());
            }
            if (!mappa.containsKey(storicoDettaglio.getProdotto().getIdProdotto())) {
                mappa.put(storicoDettaglio.getProdotto().getIdProdotto(),
                        buildPerGroup(storicoDettaglio));
            }
        }
        for (StoricoTotaleGroupDto storico : mappa.values()) {

            UnitaDiMisura
                    unitaDiMisura =
                    convertiUnitaMisuraPerDto((int) Math.round(storico.getQuantitaProdotto()),
                            storico.getUnitaDiMisura().isSonoVolume());
            Double
                    quantita =
                    convertiQuantitaGenerico(storico.getQuantitaProdotto(),
                            storico.getUnitaDiMisura(),
                            unitaDiMisura);
            mappa.get(storico.getIdProdotto()).setQuantitaProdotto(quantita);
            mappa.get(storico.getIdProdotto()).setUnitaDiMisura(unitaDiMisura);

            String messaggio = "Queste sono tutte le aggiunte non ancora effettuate per il bagno.";
            mappa.get(storico.getIdProdotto()).setRispostaCalcoloFront(messaggio);
        }

        return new ArrayList<>(mappa.values());
    }

    private RispostaTempoGenerale rispostaTempoBuilder(LocalDate dataControllo,
                                                       LocalDate storicoGeneraleTempoLast,
                                                       List<Long> idStoricoGeneraleList) {
        Bagno bagno = storicoGeneraleRepository.findById(idStoricoGeneraleList.getFirst())
                .orElseThrow()
                .getBagno();
        RispostaTempoGenerale risposta = RispostaTempoGenerale.builder()
                .nomeBagno(bagno.getNome())
                .idBagno(bagno.getIdBagno())
                .dataControlloPrecedente(storicoGeneraleTempoLast)
                .dataControlloTempo(dataControllo)
                .rispostaCalcoloFront("alimentazione calcolata in data: " + LocalDate.now())
                .numeroDiAggiunteCalcolate(idStoricoGeneraleList.size())
                .idStoricoGeneraleList(idStoricoGeneraleList)
                .build();

        List<RispostaDettaglio> rispostaDettaglioList = new ArrayList<>();
        List<Long> idStoricoDettaglioList = new ArrayList<>();
        Map<Prodotto, Integer> mappa = new HashMap<>();
        for (Long id : idStoricoGeneraleList) {
            List<StoricoDettaglio> storicoDettaglioList =
                    storicoDettaglioRepository.findByStoricoGeneraleIdStorico(id);
            if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
                continue;
            }
            for (StoricoDettaglio dettaglio : storicoDettaglioList) {
                idStoricoDettaglioList.add(dettaglio.getIdStoricoDettaglio());
                Prodotto key = dettaglio.getProdotto();
                mappa.put(key, mappa.getOrDefault(key, 0) + dettaglio.getQuantita());
            }
            for (Map.Entry<Prodotto, Integer> entry : mappa.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                UnitaDiMisura unitaDto = convertiUnitaMisuraPerDto(entry.getValue(),
                        entry.getKey().getSonoVolume());
                UnitaDiMisura unitaDb = UnitaDiMisura.MG;
                if (entry.getKey().getSonoVolume()) {
                    unitaDb = UnitaDiMisura.ML;
                }
                Double quantita = convertiQuantitaGenerico(
                        entry.getValue(),
                        unitaDb,
                        unitaDto);
                RispostaDettaglio rispostaDettaglio = RispostaDettaglio.builder()
                        .quantitaProdotto(quantita)
                        .unitaDiMisura(unitaDto)
                        .nomeProdotto(entry.getKey().getNome())
                        .build();
                rispostaDettaglioList.add(rispostaDettaglio);
            }
        }
        risposta.setDettaglioList(rispostaDettaglioList);
        risposta.setIdStoricoDettaglioList(idStoricoDettaglioList);

        return risposta;


    }

    private StoricoTotaleGroupDto buildPerGroup(StoricoDettaglio dettaglio) {
        List<Long> idStoricoDettaglioList = new ArrayList<>();
        idStoricoDettaglioList.add(dettaglio.getIdStoricoDettaglio());

        return StoricoTotaleGroupDto.builder()
                .idBagno(dettaglio.getStoricoGenerale().getBagno().getIdBagno())
                .nomeBagno(dettaglio.getStoricoGenerale().getBagno().getNome())
                .idAlimentazione(dettaglio.getStoricoGenerale()
                        .getAlimentazione()
                        .getIdAlimentazione())
                .tipologiaAggiunta(dettaglio.getStoricoGenerale()
                        .getTipologiaAggiunta())
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .nomeProdotto(dettaglio.getProdotto().getNome())
                .quantitaProdotto((double) dettaglio.getQuantita())
                .unitaDiMisura(dettaglio.getUnitaDiMisura())
                .idStoricoDettaglioList(idStoricoDettaglioList).build();
    }

    private void calcolaAlimentazioneControlliApprovati(Long idBagno) {
        // todo:impostare i controlli a monte di tutti i metodi
        Bagno bagno = bagnoService.modelRicercaId(idBagno);
        if (bagno.getAlimentazioneList().stream().noneMatch(
                alimentazione -> alimentazione.getTempo() == null ||
                        alimentazione.getTempo().isEmpty())) {
            throw new RuntimeException(
                    "Alimentazione a scatti non trovata per bagno " + idBagno);
        }
    }

}
