package org.galvanica.service.gestisciAggiunte;

import org.galvanica.model.StoricoDettaglio;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.service.operazioniBagno.StoriciAnnullaOConcludiService;

import java.util.List;

public class GestisciAggiunteFromFrontToDB {


    private final StoriciAnnullaOConcludiService storiciAnnullaOConcludiService;
    private final StoricoDettaglioRepository storicoDettaglioRepository;

    public GestisciAggiunteFromFrontToDB(
            StoriciAnnullaOConcludiService storiciAnnullaOConcludiService,
            StoricoDettaglioRepository storicoDettaglioRepository) {
        this.storiciAnnullaOConcludiService = storiciAnnullaOConcludiService;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public void eseguiListaAggiunte(List<Long> idDettaglioList) {
        for (Long id : idDettaglioList) {
            storiciAnnullaOConcludiService.eseguiSingolaAggiunta(id);
        }
    }

    public void eseguiTutteAggiunteByBagno(Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return;
        }
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            storiciAnnullaOConcludiService
                    .eseguiSingolaAggiunta(storicoDettaglio.getIdStoricoDettaglio());
        }
    }

    public void annullaListaAggiunte(List<Long> idDettaglioList) {
        for (Long id : idDettaglioList) {
            storiciAnnullaOConcludiService.escludiSingolaAggiunta(id);
        }
    }

    public void annullaTutteAggiunteByBagno(Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return;
        }
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            storiciAnnullaOConcludiService
                    .escludiSingolaAggiunta(storicoDettaglio.getIdStoricoDettaglio());
        }
    }

    public void eseguiListaAggiunteGroup(List<List<Long>> idDettaglioListList) {
        List<Long> idDettaglioList = idDettaglioListList.stream()
                .flatMap(List::stream)
                .toList();
        for (Long id : idDettaglioList) {
            storiciAnnullaOConcludiService.eseguiSingolaAggiunta(id);
        }
    }

    public void annullaListaAggiunteGroup(List<List<Long>> idDettaglioListList) {
        List<Long> idDettaglioList = idDettaglioListList.stream()
                .flatMap(List::stream)
                .toList();
        for (Long id : idDettaglioList) {
            storiciAnnullaOConcludiService.escludiSingolaAggiunta(id);
        }
    }

}
