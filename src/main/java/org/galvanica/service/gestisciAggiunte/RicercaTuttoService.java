package org.galvanica.service.gestisciAggiunte;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RicercaTuttoService {


    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private GestisciAggiunteFromFrontToDB gestisciAggiunteFromFrontToDB;

    public RicercaTuttoService(
            StoricoDettaglioRepository storicoDettaglioRepository) {
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireTotali() {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireTotali();
        return GetisciAggiunteMetodiComuni
                .getStoricoTotaleSingoloDtoList(storicoDettaglioList);
    }

    public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireGroupTotali() {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireTotali();
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        return GetisciAggiunteMetodiComuni
                .getStoricoTotaleGroupDtoList(storicoDettaglioList);
    }

    public void eseguiTutteAggiunteTotali() {
        gestisciAggiunteFromFrontToDB.eseguiTutteAggiunteTotali();
    }

    public void eseguiListaAggiunte(List<Long> idDettaglioList) {
        gestisciAggiunteFromFrontToDB.eseguiListaAggiunte(idDettaglioList);
    }

    public void annullaListaAggiunte(List<Long> idDettaglioList) {
        gestisciAggiunteFromFrontToDB.annullaListaAggiunte(idDettaglioList);
    }


    public void eseguiListaAggiunteGroup(List<List<Long>> idDettaglioListList) {
        gestisciAggiunteFromFrontToDB.eseguiListaAggiunteGroup(idDettaglioListList);
    }

    public void annullaListaAggiunteGroup(List<List<Long>> idDettaglioListList) {
        gestisciAggiunteFromFrontToDB.annullaListaAggiunteGroup(idDettaglioListList);
    }

    public void annullaTutteAggiunteTotali() {
        gestisciAggiunteFromFrontToDB.annullaTutteAggiunteTotali();
    }

/*
    //Viene richiesto di selezionare un bagno specifico.

    //A questo punto verranno visualizzate tutte le aggiunte non ancora gestite per ordine:
    //prodotto, quantità, unità di misura, annulla, conferma, note, data

    //In alto vi sarà un bottone con un flag per “group by prodotto”
    // che sommerà tutte le aggiunte dello stesso prodotto (esempio: 200 ml di A + 300 ml di B + 200 ml di A diventano: 400 ml di A + 300 ml di B)  tale pulsante elimina il campo note e data.

    //sulla tabella, in alto, si potrà flaggare in la colonna annulla o conferma per selezionare su tutte le aggiunte “annulla” o “conferma”.
    //accanto ad ogni aggiunta vi sarà un flag per annullare o confermare le aggiunte singolarmente
    //a fondo pagina un pulsante grande “Salva”.
    //prima del salvataggio (o di un reindirizzamento della pagina) sarebbe bene far apparire un messaggio di avviso.

 */
}
