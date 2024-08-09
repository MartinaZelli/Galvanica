package org.galvanica.service.gestisciAggiunte;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RicercaPerBagnoService {

	private final BagnoService bagnoService;
	private final StoricoDettaglioRepository storicoDettaglioRepository;
	private GestisciAggiunteFromFrontToDB gestisciAggiunteFromFrontToDB;

	public RicercaPerBagnoService(BagnoService bagnoService, StoricoDettaglioRepository storicoDettaglioRepository) {
		this.bagnoService = bagnoService;
		this.storicoDettaglioRepository = storicoDettaglioRepository;
	}

	public List<BagnoDto> selezionaBagno() {
		return bagnoService.findAllBagno();
	}


	public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByBagno(Long idBagno) {
		List<StoricoDettaglio> storicoDettaglioList =
			storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(idBagno);
		return GestisciAggiunteMetodiComuni.getStoricoTotaleSingoloDtoList(storicoDettaglioList);
	}

	public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireGroupByBagno(Long idBagno) {
		List<StoricoDettaglio> storicoDettaglioList =
			storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(idBagno);
		if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
			return new ArrayList<>();
		}
		return GestisciAggiunteMetodiComuni.getStoricoTotaleGroupDtoList(storicoDettaglioList);
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
