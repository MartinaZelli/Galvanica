package org.galvanica.service.gestisciAggiunte;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RicercaPerDataService {

    /*viene richiesta una specifica data o un range di date.
viene quindi visualizzato, diviso per card di ogni bagno, le aggiunte ancora da gestire.
possono essere gestite come per ricerca del bagno gestendo le aggiunte:
complessivamente, per bagno oppure per singola aggiunta.
anche qui grande pulsante salva e avviso prima di reindirizzamento e/o salvataggio.
*/

    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final BagnoService bagnoService;
    private GestisciAggiunteFromFrontToDB gestisciAggiunteFromFrontToDB;

    public RicercaPerDataService(
            StoricoDettaglioRepository storicoDettaglioRepository,
            BagnoService bagnoService) {
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.bagnoService = bagnoService;
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByDate(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByDate(
                        dataInizio,
                        dataFine);
        return GetisciAggiunteMetodiComuni
                .getStoricoTotaleSingoloDtoList(storicoDettaglioList);
    }

    public List<BagnoDto> selezionaBagno() {
        return bagnoService.findAllBagno();
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByDateEBagno(
            LocalDateTime dataInizio, LocalDateTime dataFine, Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByDateEBagno(
                        dataInizio,
                        dataFine,
                        idBagno);
        return GetisciAggiunteMetodiComuni
                .getStoricoTotaleSingoloDtoList(storicoDettaglioList);
    }

    public Map<Long, List<StoricoTotaleSingoloDto>> mostraAggiunteDaGestireByDateTotali(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        List<BagnoDto> bagnoDtoList = selezionaBagno();
        Map<Long, List<StoricoTotaleSingoloDto>> risposta = new HashMap<>();
        for (BagnoDto b : bagnoDtoList) {
            List<StoricoTotaleSingoloDto> storicoTotaleSingoloDtoList =
                    (mostraAggiunteDaGestireByDateEBagno(
                            dataInizio,
                            dataFine,
                            b.getIdBagno()));
            risposta.put(b.getIdBagno(), storicoTotaleSingoloDtoList);
        }
        return risposta;
    }

    public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireGroupByDateEBagno(
            LocalDateTime dataInizio, LocalDateTime dataFine, Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByDateEBagno(
                        dataInizio,
                        dataFine,
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        return GetisciAggiunteMetodiComuni
                .getStoricoTotaleGroupDtoList(storicoDettaglioList);
    }

    public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireGroupByDate(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        List<Map<String, Object>> listaQuery = storicoDettaglioRepository
                .listaStoriciDaGestireGroupByDate(dataInizio, dataFine);
        return GetisciAggiunteMetodiComuni.queryTransformerByDate(listaQuery);
    }

    public void eseguiListaAggiunte(List<Long> idDettaglioList) {
        gestisciAggiunteFromFrontToDB.eseguiListaAggiunte(idDettaglioList);
    }

    public void eseguiTutteAggiunteByBagno(Long idBagno) {
        gestisciAggiunteFromFrontToDB.eseguiTutteAggiunteByBagno(idBagno);
    }

    public void annullaListaAggiunte(List<Long> idDettaglioList) {
        gestisciAggiunteFromFrontToDB.annullaListaAggiunte(idDettaglioList);
    }

    public void annullaTutteAggiunteByBagno(Long idBagno) {
        gestisciAggiunteFromFrontToDB.annullaTutteAggiunteByBagno(idBagno);
    }

    public void eseguiListaAggiunteGroup(List<List<Long>> idDettaglioListList) {
        gestisciAggiunteFromFrontToDB.eseguiListaAggiunteGroup(idDettaglioListList);
    }

    public void annullaListaAggiunteGroup(List<List<Long>> idDettaglioListList) {
        gestisciAggiunteFromFrontToDB.annullaListaAggiunteGroup(idDettaglioListList);
    }

    /*
    Ricerca per data inserimento
viene richiesta una specifica data o un range di date.
viene quindi visualizzato, diviso per card(?) di ogni bagno,
le aggiunte ancora da gestire.
possono essere gestite come per ricerca del bagno gestendo le aggiunte:
complessivamente, per bagno oppure per singola aggiunta.
anche qui grande pulsante salva e avviso prima di reindirizzamento e/o salvataggio.

     */


}
