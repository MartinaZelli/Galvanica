package org.galvanica.service.operazioniBagno;

import org.galvanica.dto.AlimentazioneManualeDettaglioDto;
import org.galvanica.dto.AlimentazioneManualeGeneraleDto;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.dto.dtoConModel.ProdottoDto;
import org.galvanica.dto.risposta.InformazioniScattiPerBagno;
import org.galvanica.math.ConvertitoreUnitaMisura;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.Bagno;
import org.galvanica.model.Prodotto;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.CRUD.ProdottoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlimentazioneManualeService {
	private final BagnoService bagnoService;
	private final StoricoDettaglioRepository storicoDettaglioRepository;
	private final StoricoGeneraleRepository storicoGeneraleRepository;
	private final ProdottoService prodottoService;

	public AlimentazioneManualeService(BagnoService bagnoService,
		StoricoDettaglioRepository storicoDettaglioRepository,
		StoricoGeneraleRepository storicoGeneraleRepository,
		ProdottoService prodottoService) {
		this.bagnoService = bagnoService;
		this.storicoDettaglioRepository = storicoDettaglioRepository;
		this.storicoGeneraleRepository = storicoGeneraleRepository;
		this.prodottoService = prodottoService;
	}

	public void creaAggiuntaManuale(AlimentazioneManualeGeneraleDto generale) {
		controlliDto(generale);
		StoricoGenerale storicoGenerale = creaAggiuntaGenerale(generale);
		List<AlimentazioneManualeDettaglioDto> dettaglioList = generale.getDettaglioList();
		if (dettaglioList == null || dettaglioList.isEmpty()) {
			return;
		}
		for (AlimentazioneManualeDettaglioDto dettaglio : dettaglioList) {
			verificaProdottoInserimentoManuale(generale.getIdBagno(), dettaglio.getIdProdotto());
			verificaPresenzaUnitaMisura(dettaglio.getQuantita(), dettaglio.getUnitaDiMisura());
			creaAggiuntaDettaglio(dettaglio, storicoGenerale);
		}
	}

	private StoricoGenerale creaAggiuntaGenerale(AlimentazioneManualeGeneraleDto generale) {
		Bagno bagno = bagnoService.modelRicercaId(generale.getIdBagno());
		return storicoGeneraleRepository.save((StoricoGenerale
			.builder()
			.bagno(bagno)
			.scattiTotaliBagno(generale.getScattiTotaliBagno())
			.restoScattiBagno(generale.getRestoScattiBagno())
			.scattiInseriti(generale.getScattiInseriti())
			.tipologiaAggiunta(TipologiaAggiunta.MANUALE)
			.note(generale.getNote())
			.build()));
	}

	private void creaAggiuntaDettaglio(AlimentazioneManualeDettaglioDto dettaglio,
		StoricoGenerale generale) {
		Prodotto prodotto = prodottoService.modelRicercaId(dettaglio.getIdProdotto());
		UnitaDiMisura unitaDiMisura = UnitaDiMisura.MG;
		if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
			unitaDiMisura = UnitaDiMisura.ML;
		}

		storicoDettaglioRepository.save(StoricoDettaglio
			.builder()
			.prodotto(prodotto)
			.storicoGenerale(generale)
			.quantita(ConvertitoreUnitaMisura.convertiQuantitaToDatabase(dettaglio.getQuantita(),
				dettaglio.getUnitaDiMisura()))
			.unitaDiMisura(unitaDiMisura)
			.build());

	}

	private void controlliDto(AlimentazioneManualeGeneraleDto generale) {
		bagnoService.modelRicercaId(generale.getIdBagno());
		if (generale.getScattiTotaliBagno() == null) {
			throw new RuntimeException("non sono stati inseriti gli scatti Totali del bagno");
		}
		if (generale.getRestoScattiBagno() == null) {
			throw new RuntimeException("non sono stati inseriti gli scatti di resto del bagno");
		}
		if (generale.getScattiInseriti() == null) {
			generale.setScattiInseriti(0);
		}

	}

	private void verificaProdottoInserimentoManuale(Long idBagno, Long idProdotto) {
		List<ProdottoDto> prodottoDtoList = prodottoService.ricercaProdottiByBagno(idBagno);
		boolean prodottoValido = prodottoDtoList
			.stream()
			.anyMatch(prodottoDto -> prodottoDto.getIdProdotto().equals(idProdotto));
		if (!prodottoValido) {
			throw new RuntimeException("il prodotto inserito non è legato al bagno di destinazione.");
		}
	}

	private void verificaPresenzaUnitaMisura(double quantita, UnitaDiMisura unitaDiMisura) {
		if (quantita == 0D) {
			throw new RuntimeException("la quantità inserita non può essere 0");
		}
		if (unitaDiMisura == null) {
			throw new RuntimeException(
				"l'unita di misura deve essere valorizzata se è inserita una quantità");
		}
	}

	public List<BagnoDto> selezionaBagno() {
		return bagnoService.findAllBagno();
	}

	public InformazioniScattiPerBagno informazioniScattiPerBagno(Long idBagno) {
		StoricoGenerale storicoGenerale =
			storicoGeneraleRepository.ultimoStoricoGeneraleScatti(idBagno);

		return InformazioniScattiPerBagno
			.builder()
			.idBagno(idBagno)
			.restoScattiBagno(storicoGenerale.getRestoScattiBagno())
			.scattiTotaliBagno(storicoGenerale.getScattiTotaliBagno())
			.build();
	}

	public List<ProdottoDto> ricercaProdottiByBagno(Long id) {
		return prodottoService.ricercaProdottiByBagno(id);
	}

	public List<UnitaDiMisura> selezionaUnitaDiMisura(Boolean sonoVolume) {
		List<UnitaDiMisura> unitaDiMisuraList = new ArrayList<>();
		for (UnitaDiMisura unita : UnitaDiMisura.values()) {
			if (unita.isSonoVolume() == sonoVolume) {
				unitaDiMisuraList.add(unita);
			}
		}
		return unitaDiMisuraList;
	}


}
