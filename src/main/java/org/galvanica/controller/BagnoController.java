package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.StoriciAnnullaOConcludiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bagno")
public class BagnoController {

	private static final Long LIMITE_LISTA = 10L;
	private final BagnoService service;
	private final StoriciAnnullaOConcludiService storiciAnnullaOConcludiService;

	public BagnoController(BagnoService service,
		StoriciAnnullaOConcludiService storiciAnnullaOConcludiService) {
		this.service = service;
		this.storiciAnnullaOConcludiService = storiciAnnullaOConcludiService;
	}

	@PostMapping
	public String inserisciBagno(@RequestBody BagnoDto bagnoDto, Model model) {
		service.inserisci(bagnoDto);
		return listaBagni(model);
	}

	@GetMapping("/new")
	public String newBagno(Model model) {
		return "bagno/bagnoNuovo";
	}

	@GetMapping("/list")
	public String listaBagni(Model model) {
		List<BagnoDto> bagnoDtoList = service.findAllBagno();
		model.addAttribute("bagnoList", bagnoDtoList);
		return "bagno/bagnoList";
	}

	@PutMapping("{id}")
	public String aggiornaBagno(@RequestBody BagnoDto bagnoDto, @PathVariable Long id,
		Model model) {
		bagnoDto.setIdBagno(id);
		service.aggiorna(bagnoDto);
		return listaBagni(model);
	}

	@GetMapping("/azioni/{id}")
	public String listaBagni(@PathVariable Long id, Model model) {
		BagnoDto bagnoDto = service.ricercaId(id).orElseThrow();
		model.addAttribute("bagno", bagnoDto);
		return "bagno/bagnoAzioni";
	}

	@DeleteMapping("{id}")
	public String eliminaBagno(@PathVariable Long id, Model model) {
		service.elimina(id);
		return listaBagni(model);
	}

	@GetMapping("prova/{id}")
	public Optional<BagnoDto> ricercaId(@PathVariable Long id) {
		return service.ricercaId(id);
	}

	/*
	 *
	 * @PutMapping("{id}/alimentazioneScatti/{scattiParziali}")
	 * public AlimentazioneRispostaDto alimentazioneScatti(@PathVariable Long id,
	 *
	 * @PathVariable Integer scattiParziali) {
	 * return alimentazioneAScattiService.creaNuovaAlimentazione(id,
	 * scattiParziali);
	 * }
	 *
	 * @PutMapping("{id}/alimentazioneTempo/{dataControllo}")
	 * public AlimentazioneRispostaDto alimentazioneTempo(@PathVariable Long id,
	 *
	 * @PathVariable LocalDate dataControllo) {
	 * return alimentazioneATempoService.calcolaAlimentazione(id,
	 * dataControllo);
	 * }
	 */
	@PutMapping("eseguiSingolaAggiunta/{idStoricoDettaglio}")
	public void eseguiSingolaAggiunta(@PathVariable Long idStoricoDettaglio) {
		storiciAnnullaOConcludiService.eseguiSingolaAggiunta(idStoricoDettaglio);
	}

	@PutMapping("confermaInteraAlimentazione/{idStoricoGenerale}")
	public void confermaInteraAlimentazione(@PathVariable Long idStoricoGenerale) {
		System.out.println("dentro confermaInteraAlimentazione");
		storiciAnnullaOConcludiService.confermaInteraAlimentazione(idStoricoGenerale);
	}

	@GetMapping("vedi/{id}")
	public String vediBagno(@PathVariable Long id, Model model) {
		model.addAttribute("bagno", service.modelRicercaId(id));
		return "bagnoModifica :: bagnoVedi";
	}

	@GetMapping("{id}")
	public String bagnoGenerale(@PathVariable Long id, Model model) {
		model.addAttribute("bagno", service.modelRicercaId(id));
		model.addAttribute("storicoList", service.storicoGeneraleListByBagno(id, LIMITE_LISTA));
		return "bagnoGenerale";
	}
}
