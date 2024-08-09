package org.galvanica.service.gestisciAggiunte;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.dto.dtoConModel.ProdottoDto;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.CRUD.ProdottoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RicercaPerProdottoService {

    private final BagnoService bagnoService;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final ProdottoService prodottoService;
    private GestisciAggiunteFromFrontToDB gestisciAggiunteFromFrontToDB;

    public RicercaPerProdottoService(BagnoService bagnoService,
                                     StoricoDettaglioRepository storicoDettaglioRepository,
                                     ProdottoService prodottoService) {
        this.bagnoService = bagnoService;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.prodottoService = prodottoService;
    }

    public List<ProdottoDto> selezionaProdotto() {
        return prodottoService.findAllProdotto();
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByProdotto(
            Long idProdotto) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByProdotto(
                        idProdotto);
        return GetisciAggiunteMetodiComuni
                .getStoricoTotaleSingoloDtoList(storicoDettaglioList);
    }

    public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireByProdottoGroupBagno(
            Long idProdotto) {
        List<Map<String, Object>> listaQuery = storicoDettaglioRepository
                .listaStoriciDaGestireGroupByProdotto(idProdotto);
        return GetisciAggiunteMetodiComuni.queryTransformerPerGroup(listaQuery);
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
    Ricerca per prodotto
Viene richiesto di selezionare uno specifico prodotto.
vengono quindi aperte una o più card di ogni bagno che
utilizza tale prodotto che ha aggiunte da gestire.
anche qui, come per data inserimento, possiamo gestire ogni aggiunta complessivamente,
per bagno oppure per singola aggiunta.

 */
}
