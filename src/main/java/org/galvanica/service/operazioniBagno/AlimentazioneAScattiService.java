package org.galvanica.service.operazioniBagno;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.RisultatoOperazioniAScatti;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.*;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.galvanica.math.ConvertitoreUnitaMisura.convertiQuantitaGenerico;
import static org.galvanica.math.ConvertitoreUnitaMisura.convertiUnitaMisuraPerDto;

@Service
public class AlimentazioneAScattiService {

	private final StoricoGeneraleRepository storicoGeneraleRepository;
	private final StoricoDettaglioRepository storicoDettaglioRepository;
	private final BagnoService bagnoService;

	public AlimentazioneAScattiService(StoricoGeneraleRepository storicoGeneraleRepository,
		StoricoDettaglioRepository storicoDettaglioRepository, BagnoService bagnoService) {
		this.storicoGeneraleRepository = storicoGeneraleRepository;
		this.storicoDettaglioRepository = storicoDettaglioRepository;
		this.bagnoService = bagnoService;
	}

	public List<List<StoricoTotaleSingoloDto>> calcolaAlimentazioneList(
		Map<Long, Integer> idBagnoScattiParzialiMap) {
		List<List<StoricoTotaleSingoloDto>> risposta = new ArrayList<>();
		for (Map.Entry<Long, Integer> entry : idBagnoScattiParzialiMap.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			risposta.add(creaNuovaAlimentazione(entry.getKey(), entry.getValue()));

		}
		return risposta;
	}

	// calcola nuova Alimentazione
	public List<StoricoTotaleSingoloDto> creaNuovaAlimentazione(Long idBagno, int scattiLetti) {
		calcolaAlimentazioneControlliApprovati(idBagno);
		StoricoGenerale storicoGeneraleCreato = creaStorici(idBagno, scattiLetti);
		List<StoricoDettaglio>
			storicoDettaglioList =
			storicoGeneraleCreato.getStoricoDettaglioList();
		List<StoricoTotaleSingoloDto> risultato = new ArrayList<>();
		StoricoTotaleSingoloDto
			risposta =
			buildaStoricoTotaleSingoloDtoSoloGenerale(storicoGeneraleCreato);
		if (storicoGeneraleCreato.getMoltiplicatoreAlimentazione() == 0) {
			risposta.setRispostaCalcoloFront(
				"gli scatti sono inferiori al 90% dell'alimentazione," +
					"le aggiunte non verranno eseguite ma messe in conto per la prossima chiamata.");
			risultato.add(risposta);
			return risultato;
		}
		for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {

			UnitaDiMisura
				unitaDiMisura =
				convertiUnitaMisuraPerDto(storicoDettaglio.getQuantita(),
					storicoDettaglio.getUnitaDiMisura().isSonoVolume());
			Double
				quantita =
				convertiQuantitaGenerico(storicoDettaglio.getQuantita().doubleValue(),
					storicoDettaglio.getUnitaDiMisura(), unitaDiMisura);

			StoricoTotaleSingoloDto rispostaDettaglio = risposta.clone();
			rispostaDettaglio.setIdStoricoDettaglio(storicoDettaglio.getIdStoricoDettaglio());
			rispostaDettaglio.setQuantitaProdotto(quantita);
			rispostaDettaglio.setUnitaDiMisura(unitaDiMisura);
			rispostaDettaglio.setIdProdotto(storicoDettaglio.getProdotto().getIdProdotto());
			rispostaDettaglio.setNomeProdotto(storicoDettaglio.getProdotto().getNome());
			rispostaDettaglio.setRispostaCalcoloFront(
				"questa è l'aggiunta a scatti che prevede il bagno");

			risultato.add(rispostaDettaglio);
		}
		return risultato;
	}

	private StoricoTotaleSingoloDto buildaStoricoTotaleSingoloDtoSoloGenerale(
		StoricoGenerale generale) {
		return StoricoTotaleSingoloDto.builder().idStoricoGenerale(generale.getIdStorico())
			.idBagno(generale.getBagno().getIdBagno()).nomeBagno(generale.getBagno().getNome())
			.idAlimentazione(generale.getAlimentazione().getIdAlimentazione())
			.moltiplicatoreAlimentazione(generale.getMoltiplicatoreAlimentazione())
			.scattiTotaliBagno(generale.getScattiTotaliBagno())
			.scattiInseriti(generale.getScattiInseriti()).restoScattiPrecedenti(
				storicoGeneraleRepository.ultimoStoricoGeneraleScatti(
					generale.getBagno().getIdBagno()).getRestoScattiBagno())
			.tipologiaAggiunta(generale.getTipologiaAggiunta())
			.noteStoricoGenerale(generale.getNote()).dataCreazione(generale.getDataCreazione())
			.build();
	}

	private Integer trovaPrimoValoreVolumetrico(Alimentazione alimentazione) {

		return alimentazione.getDettaglioAlimentazioneList().stream().filter(
				dettaglioAlimentazione -> dettaglioAlimentazione.getUnitaDiMisura().isSonoVolume())
			.findFirst().map(DettaglioAlimentazione::getQuantitaProdotto).orElse(null);
	}

	private void creaStoricoDettaglioApprossimato(Alimentazione alimentazione,
		RisultatoOperazioniAScatti risultatoOperazioniAScatti, StoricoGenerale storicoGenerale) {
		for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {
			double
				quantitaProdottoAggiunta =
				dettaglio.getQuantitaProdotto() *
					risultatoOperazioniAScatti.getMoltiplicatoreAlimentazione();
			if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
				quantitaProdottoAggiunta =
					MetodiArrotondamenti.moltiplicatoreApprossimatoPerAggiunta(
						quantitaProdottoAggiunta);
			}
			storicoDettaglioRepository.save(
				StoricoDettaglio.builder().prodotto(dettaglio.getProdotto())
					.storicoGenerale(storicoGenerale)
					.quantita((int) Math.round(quantitaProdottoAggiunta))
					.unitaDiMisura(dettaglio.getUnitaDiMisura()).build());
		}

	}

	private StoricoGenerale creaStorici(Long idBagno, int scattiLetti) {
		Bagno bagno = bagnoService.modelRicercaId(idBagno);
		Alimentazione alimentazione = trovaAlimentazioneScatti(bagno);
		StoricoGenerale
			ultimoStoricoGenerale =
			storicoGeneraleRepository.ultimoStoricoGeneraleScatti(idBagno);
		Integer primoValoreVolumetrico = trovaPrimoValoreVolumetrico(alimentazione);
		RisultatoOperazioniAScatti
			risultatoOperazioniAScatti =
			MetodiArrotondamenti.operazioniAScatti(scattiLetti, alimentazione.getScatti(),
				alimentazione.getValoriArrotondati(), primoValoreVolumetrico);
		Integer scattiTotali = ultimoStoricoGenerale.getScattiTotaliBagno() + scattiLetti;

		StoricoGenerale
			storicoGenerale =
			StoricoGenerale.builder().bagno(bagno).scattiTotaliBagno(scattiTotali)
				.scattiInseriti(scattiLetti).alimentazione(alimentazione)
				.tipologiaAggiunta(TipologiaAggiunta.SCATTI).moltiplicatoreAlimentazione(
					risultatoOperazioniAScatti.getMoltiplicatoreAlimentazione()).build();

		if (risultatoOperazioniAScatti.getMoltiplicatoreAlimentazione() == 0) {
			storicoGenerale.setRestoScattiBagno(
				ultimoStoricoGenerale.getRestoScattiBagno() + scattiLetti);
			return storicoGeneraleRepository.save(storicoGenerale);
		}

		storicoGenerale.setRestoScattiBagno((int) risultatoOperazioniAScatti.getRestoScatti());
		storicoGenerale = storicoGeneraleRepository.save(storicoGenerale);

		creaStoricoDettaglioApprossimato(alimentazione, risultatoOperazioniAScatti,
			storicoGenerale);

		storicoGenerale =
			storicoGeneraleRepository.findById(storicoGenerale.getIdStorico()).orElseThrow();

		return storicoGenerale;
	}

	// calcola tutte le aggiunte da Storico

	public List<StoricoTotaleGroupDto> calcolaAggiunteStoricoPerIdBagno(Long idBagno) {
		calcolaAlimentazioneControlliApprovati(idBagno);

		Map<Long, StoricoTotaleGroupDto> mappa = new HashMap<>();
		List<StoricoGenerale>
			storicoGeneraleListDaEseguire =
			storicoGeneraleRepository.storicoGeneraleDescList(false, idBagno,
				TipologiaAggiunta.SCATTI);

		List<StoricoDettaglio>
			storicoDettaglioList =
			storicoGeneraleListDaEseguire.stream().filter(
					storicoGenerale -> storicoGenerale.getStoricoDettaglioList() != null &&
						!storicoGenerale.getStoricoDettaglioList().isEmpty())
				.map(StoricoGenerale::getStoricoDettaglioList).flatMap(Collection::parallelStream)
				.filter(storicoDettaglio -> !storicoDettaglio.getEseguitoDettaglio() &&
					!storicoDettaglio.getAnnullatoDettaglio()).toList();

		for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
			if (mappa.containsKey(storicoDettaglio.getProdotto().getIdProdotto())) {
				Long key = storicoDettaglio.getProdotto().getIdProdotto();
				Double
					quantitaProdotto =
					mappa.get(key).getQuantitaProdotto() + storicoDettaglio.getQuantita();

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
				convertiQuantitaGenerico(storico.getQuantitaProdotto(), storico.getUnitaDiMisura(),
					unitaDiMisura);
			mappa.get(storico.getIdProdotto()).setQuantitaProdotto(quantita);
			mappa.get(storico.getIdProdotto()).setUnitaDiMisura(unitaDiMisura);

			String messaggio = "Queste sono tutte le aggiunte non ancora effettuate per il bagno.";
			mappa.get(storico.getIdProdotto()).setRispostaCalcoloFront(messaggio);
		}

		return new ArrayList<>(mappa.values());
	}

	private StoricoTotaleGroupDto buildPerGroup(StoricoDettaglio dettaglio) {
		List<Long> idStoricoDettaglioList = new ArrayList<>();
		idStoricoDettaglioList.add(dettaglio.getIdStoricoDettaglio());

		return StoricoTotaleGroupDto.builder()
			.idBagno(dettaglio.getStoricoGenerale().getBagno().getIdBagno())
			.nomeBagno(dettaglio.getStoricoGenerale().getBagno().getNome())
			.idAlimentazione(dettaglio.getStoricoGenerale().getAlimentazione().getIdAlimentazione())
			.tipologiaAggiunta(dettaglio.getStoricoGenerale().getTipologiaAggiunta())
			.idProdotto(dettaglio.getProdotto().getIdProdotto())
			.nomeProdotto(dettaglio.getProdotto().getNome())
			.quantitaProdotto((double) dettaglio.getQuantita())
			.unitaDiMisura(dettaglio.getUnitaDiMisura())
			.idStoricoDettaglioList(idStoricoDettaglioList).build();
	}
	// metodi comuni

	private void calcolaAlimentazioneControlliApprovati(Long idBagno) {
		// todo:impostare i controlli a monte di tutti i metodi
		Bagno bagno = bagnoService.modelRicercaId(idBagno);
		if (bagno.getAlimentazioneList().stream()
			.noneMatch(alimentazione -> alimentazione.getScatti() != 0)) {
			throw new RuntimeException("Alimentazione a scatti non trovata per bagno " + idBagno);
		}
		if (storicoGeneraleRepository.ultimoStoricoGeneraleScatti(idBagno) == null) {
			throw new RuntimeException(
				"Non vi è nessuno storicoGenerale relativo agli scatti per il bagno " + idBagno);
		}
	}

	private Alimentazione trovaAlimentazioneScatti(Bagno bagno) {
		return bagno.getAlimentazioneList().stream()
			.filter(alimentazioneFilter -> alimentazioneFilter.getScatti() != null).findFirst()
			.orElseThrow(() -> new RuntimeException(
				"Alimentazione a scatti non trovata per bagno " +
					bagno.getIdBagno() +
					" nome: " +
					bagno.getNome()));
	}

}
