package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.AlimentazioneDto;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.galvanica.math.TipologiaAggiunta.SCATTI;


@Service
public class AlimentazioneService implements ICRUDService<AlimentazioneDto, Alimentazione> {

	private final AlimentazioneRepository alimentazioneRepository;
	private final BagnoRepository bagnoRepository;

	public AlimentazioneService(AlimentazioneRepository alimentazioneRepository,
		BagnoRepository bagnoRepository) {
		this.alimentazioneRepository = alimentazioneRepository;
		this.bagnoRepository = bagnoRepository;
	}

	public Double calcolaMoltiplicatore(@PathVariable Long idBagno, int scatti) {
		var alimentazione = alimentazioneRepository
			.findByBagnoIdBagnoAndTipologiaAggiunta(idBagno, SCATTI)
			.stream()
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Alimentazione non trovata"));
		var scattiAlimentazione = alimentazione.getScatti();

		return (scatti / (double) scattiAlimentazione);
	}


	@Override
	public AlimentazioneDto inserisci(AlimentazioneDto elemento) {
		if (elemento.getIdAlimentazione() != null) {
			throw new RuntimeException("l'id deve essere autoincrementale, non inizializzare");
		}

		Bagno bagno = validaAlimentazione(elemento);

		if (Objects.equals(elemento.getTipologiaAggiunta(), SCATTI)) {
			if (bagno.getAlimentazioneList() != null) {
				if (bagno
					.getAlimentazioneList()
					.stream()
					.anyMatch(alimentazione -> alimentazione.getScatti() != null)) {
					throw new RuntimeException(
						"esiste già un alimentazione a scatti per questo bagno.");
				}
			}
		}

		Alimentazione alimentazione = Alimentazione
			.builder()
			.bagno(bagno)
			.tempo(elemento.getTempo())
			.scatti(elemento.getScatti())
			.descrizione(elemento.getDescrizione())
			.valoriArrotondati(elemento.getValoriArrotondati())
			.build();
		alimentazione = alimentazioneRepository.save(alimentazione);
		return fromModelToDto(alimentazione);
	}

	@Override
	public Boolean elimina(long id) {
		boolean risposta = alimentazioneRepository.findById(id).isPresent();
		if (risposta) {
			alimentazioneRepository.deleteById(id);
		}
		return risposta;
	}

	@Override
	public AlimentazioneDto aggiorna(AlimentazioneDto elemento) {
		Optional<Alimentazione> alimentazioneOptional =
			alimentazioneRepository.findById(elemento.getIdAlimentazione());
		if (alimentazioneOptional.isEmpty()) {
			throw new RuntimeException("mettere un id corretto");
		}
		if (!Objects.equals(elemento.getIdBagno(),
			alimentazioneOptional.get().getBagno().getIdBagno())) {
			throw new RuntimeException(
				"non può essere modificato il bagno relativo all'alimentazione.");
		}
		Bagno bagno = validaAlimentazione(elemento);
		//todo:estrarre da validaAlimentazione la chiamata al repository e riportarla fuori dal metodo.

		if (!Objects.equals(elemento.getTipologiaAggiunta(),
			alimentazioneOptional.get().getTipologiaAggiunta())) {
			throw new RuntimeException(
				"non può essere modificata la tipologia di aggiunta di un bagno.");
		}

		Alimentazione alimentazione = alimentazioneOptional.get();
		alimentazione.setDescrizione(elemento.getDescrizione());
		alimentazione.setTempo(elemento.getTempo());
		alimentazione.setScatti(elemento.getScatti());
		alimentazione.setBagno(bagno);
		alimentazione.setValoriArrotondati(elemento.getValoriArrotondati());
		alimentazione = alimentazioneRepository.save(alimentazione);

		return fromModelToDto(alimentazione);
	}

	@Override
	public Optional<AlimentazioneDto> ricercaId(long id) {
		Optional<Alimentazione> alimentazioneOptional = alimentazioneRepository.findById(id);
		return alimentazioneOptional.map(this::fromModelToDto);
	}

	@Override
	public AlimentazioneDto fromModelToDto(Alimentazione oggettoDaTrasformare) {
		return AlimentazioneDto
			.builder()
			.idAlimentazione(oggettoDaTrasformare.getIdAlimentazione())
			.idBagno(oggettoDaTrasformare.getBagno().getIdBagno())
			.scatti(oggettoDaTrasformare.getScatti())
			.tempo(oggettoDaTrasformare.getTempo())
			.descrizione(oggettoDaTrasformare.getDescrizione())
			.valoriArrotondati(oggettoDaTrasformare.getValoriArrotondati())
			.nomeBagno(oggettoDaTrasformare.getBagno().getNome())
			.tipologiaAggiunta(oggettoDaTrasformare.getTipologiaAggiunta())
			.build();
	}

	public List<AlimentazioneDto> findAllAlimentazione() {
		return StreamSupport
			.stream(alimentazioneRepository.findAll().spliterator(), false)
			.map(this::fromModelToDto)
			.collect(Collectors.toList());
	}

	private Bagno validaAlimentazione(AlimentazioneDto elemento) {
		if (elemento.getIdBagno() == null) {
			throw new RuntimeException("l'id bagno deve essere valorizzato");
		}
		if (elemento.getScatti() == null && elemento.getTempo() == null) {
			throw new RuntimeException("deve essere valorizzato un attributo fra Scatti e Tempo");
		}
		if (elemento.getScatti() != null && elemento.getTempo() != null) {
			throw new RuntimeException("può essere valorizzato un solo attributo fra Scatti e Tempo");
		}
		if (elemento.getScatti() != null && elemento.getScatti() <= 0) {
			throw new RuntimeException(
				"non esistono aggiunte a scatti di 0 oppure con valori Negativi");
		}
		if (elemento.getValoriArrotondati() == null) {
			elemento.setValoriArrotondati(false);
		}
		if (elemento.getTipologiaAggiunta() == null) {
			throw new RuntimeException("la tipologia di aggiunta deve essere inizializzata");
		}
		if (elemento.getTipologiaAggiunta().equals(TipologiaAggiunta.MANUALE)) {
			throw new RuntimeException(
				"non può essere inizializzata un alimentazione Manuale per il bagno.");
		}
		if (elemento.getTipologiaAggiunta().equals(SCATTI)) {
			if (elemento.getScatti() == null) {
				throw new RuntimeException(
					"la Tipologia di aggiunta è scatti ma gli scatti non sono inizializzati");
			}
			if (elemento.getTempo() != null) {
				throw new RuntimeException(
					"la Tipologia di aggiunta è scatti, non può essere inizializzato il tempo.");
			}
		}
		if (elemento.getTipologiaAggiunta().equals(TipologiaAggiunta.TEMPO)) {
			if (elemento.getTempo() == null) {
				throw new RuntimeException(
					"la Tipologia di aggiunta è Tempo ma il tempo non è inizializzato");
			}
			if (elemento.getScatti() != null) {
				throw new RuntimeException(
					"la Tipologia di aggiunta è Tempo, non possono essere inizializzati gli scatti");
			}
		}
		Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
		if (bagnoOptional.isEmpty()) {
			throw new RuntimeException("l'id del bagno non esiste, correggere.");
		}
		return bagnoOptional.get();
	}


}
