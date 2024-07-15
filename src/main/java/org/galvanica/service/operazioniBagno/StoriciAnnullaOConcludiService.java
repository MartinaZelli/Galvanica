package org.galvanica.service.operazioniBagno;

import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StoriciAnnullaOConcludiService {
	private final StoricoGeneraleRepository storicoGeneraleRepository;
	private final StoricoDettaglioRepository storicoDettaglioRepository;

	// todo: impostare i controlli a monte di tutti i metodi

	public StoriciAnnullaOConcludiService(StoricoGeneraleRepository storicoGeneraleRepository,
		StoricoDettaglioRepository storicoDettaglioRepository,
		AlimentazioneRepository alimentazioneRepository) {
		this.storicoGeneraleRepository = storicoGeneraleRepository;
		this.storicoDettaglioRepository = storicoDettaglioRepository;
	}

	public void confermaInteraAlimentazione(Long idStoricoGenerale) {
		StoricoGenerale storicoGenerale = trovaStoricoGenerale(idStoricoGenerale);
		if (!storicoGenerale.getStoricoDettaglioList().isEmpty()) {
			for (StoricoDettaglio storicoDettaglio : storicoGenerale.getStoricoDettaglioList()) {
				eseguiStoricoDettaglio(storicoDettaglio);
			}
		}
		concludiStoricoGenerale(storicoGenerale);
	}

	public void eseguiSingolaAggiunta(Long idStoricoDettaglio) {
		StoricoDettaglio storicoDettaglio = trovaStoricoDettaglio(idStoricoDettaglio);
		if (storicoDettaglio.getAnnullatoDettaglio()) {
			throw new RuntimeException(
				"se il DettaglioStorico è escluso non può essere anche eseguito");
		}
		eseguiStoricoDettaglio(storicoDettaglio);
		if (listaStoricoDettaglioNONCompletata(storicoDettaglio.getStoricoGenerale())) {
			return;
			// todo: non so se conviene fare uscire qualcosa dal metodo per capire se anche
			// lo storicoGenerale è aggiornato oppure no.
			// aggiornare gli scatti solo se l-aggiunta che sto confermando e la piu vecchia
			// aggiunta da eseguire nel bagno
		}
		concludiStoricoGenerale(storicoDettaglio.getStoricoGenerale());
	}

	// metodi per confermaInteraAlimentazione e eseguiSingolaAggiunta.
	private void eseguiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
		if (storicoDettaglio.getAnnullatoDettaglio()) {
			return;
		}
		storicoDettaglio.setEseguitoDettaglio(true);
		storicoDettaglioRepository.save(storicoDettaglio);
	}

	private void concludiStoricoGenerale(StoricoGenerale storicoGenerale) {
		if (storicoGenerale.getEseguitoGenerale()) {
			throw new RuntimeException("lo storico generale è già concluso");
		}
		if (listaStoricoDettaglioNONCompletata(storicoGenerale)) {
			throw new RuntimeException("non tutti i Dettagli storico sono eseguiti o esclusi");
		}
		storicoGenerale.setEseguitoGenerale(true);
		storicoGenerale.setDataEsecuzione(LocalDateTime.now());
		storicoGeneraleRepository.save(storicoGenerale);
	}

	// metodi per tutto
	private StoricoDettaglio trovaStoricoDettaglio(Long id) {
		Optional<StoricoDettaglio>
			storicoDettaglioTrovato =
			storicoDettaglioRepository.findById(id);
		return storicoDettaglioTrovato.orElseThrow(
			() -> new RuntimeException("storico non trovato per id " + id));
	}

	private StoricoGenerale trovaStoricoGenerale(long id) {
		Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(id);
		return storicoGeneraleTrovato.orElseThrow(
			() -> new RuntimeException("storico non trovato per id " + id));
	}

	private boolean listaStoricoDettaglioNONCompletata(StoricoGenerale storicoGenerale) {
		return storicoGenerale.getStoricoDettaglioList().stream().anyMatch(
			storicoDettaglio -> !storicoDettaglio.getEseguitoDettaglio() &&
				!storicoDettaglio.getAnnullatoDettaglio());
	}

	private void annullaStoricoGenerale(StoricoGenerale storicoGenerale) {
		if (storicoGenerale.getEseguitoGenerale()) {
			throw new RuntimeException("lo storico generale è già concluso");
		}
		storicoGenerale.setEseguitoGenerale(true);
		storicoGenerale.setAnnullatoGenerale(true);
		storicoGenerale.setDataEsecuzione(LocalDateTime.now());
		storicoGeneraleRepository.save(storicoGenerale);
	}

	public void escludiSingolaAggiunta(Long idStoricoDettaglio) {
		StoricoDettaglio storicoDettaglio = trovaStoricoDettaglio(idStoricoDettaglio);
		if (storicoDettaglio.getEseguitoDettaglio()) {
			throw new RuntimeException(
				"se il DettaglioStorico è eseguito, non può essere anche escluso");
		}
		escludiStoricoDettaglio(storicoDettaglio);
		if (listaStoricoDettaglioNONCompletata(storicoDettaglio.getStoricoGenerale())) {
			return;
			// todo: non so se conviene fare uscire qualcosa dal metodo per capire se anche
			// lo storicoGenerale è aggiornato oppure no.
			// aggiornare gli scatti solo se l-aggiunta che sto confermando e la piu vecchia
			// aggiunta da eseguire nel bagno
		}
		aggiornaStoricoGenerale(storicoDettaglio.getStoricoGenerale());
	}

	private void escludiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
		if (storicoDettaglio.getEseguitoDettaglio()) {
			return;
		}
		storicoDettaglio.setAnnullatoDettaglio(true);
		storicoDettaglioRepository.save(storicoDettaglio);
	}

	private void aggiornaStoricoGenerale(StoricoGenerale storicoGenerale) {
		List<StoricoDettaglio> storicoDettaglioList = storicoGenerale.getStoricoDettaglioList();
		long
			totConclusi =
			storicoDettaglioList.stream().filter(
					dettaglio -> dettaglio.getEseguitoDettaglio() || dettaglio.getAnnullatoDettaglio())
				.count();
		long
			totEsclusi =
			storicoDettaglioList.stream().filter(StoricoDettaglio::getAnnullatoDettaglio).count();
		if (totEsclusi == storicoDettaglioList.size()) {
			annullaStoricoGenerale(storicoGenerale);
			return;
		}
		if (totConclusi == storicoDettaglioList.size()) {
			concludiStoricoGenerale(storicoGenerale);
		}
	}

	public void annullaInteraAlimentazione(Long idStoricoGenerale) {
		StoricoGenerale storicoGenerale = trovaStoricoGenerale(idStoricoGenerale);
		if (!storicoGenerale.getStoricoDettaglioList().isEmpty()) {
			for (StoricoDettaglio storicoDettaglio : storicoGenerale.getStoricoDettaglioList()) {
				if (storicoDettaglio.getEseguitoDettaglio()) {
					throw new RuntimeException("vi è almeno un aggiunta eseguita. " +
						"Per annullare l'intera alimentazione nessuna aggiunta deve essere eseguita.");
				}
				if (!storicoDettaglio.getAnnullatoDettaglio()) {
					escludiStoricoDettaglio(storicoDettaglio);
				}
			}
		}
		annullaStoricoGenerale(storicoGenerale);
	}

	public void eseguiSingolaAggiuntaList(List<Long> idStoricoDettaglioList) {
		idStoricoDettaglioList = storicoDettaglioRepository.orderAscIdList(idStoricoDettaglioList);
		for (Long idStoricoDettaglio : idStoricoDettaglioList) {
			eseguiSingolaAggiunta(idStoricoDettaglio);
		}
	}

	public void escludiSingolaAggiuntaList(List<Long> idStoricoDettaglioList) {
		for (Long idStoricoDettaglio : idStoricoDettaglioList) {
			escludiSingolaAggiunta(idStoricoDettaglio);
		}
	}
}
