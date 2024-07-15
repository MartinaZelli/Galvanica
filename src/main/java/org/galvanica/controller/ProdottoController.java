package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.ProdottoDto;
import org.galvanica.service.CRUD.MagazzinoService;
import org.galvanica.service.CRUD.ProdottoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/prodotto")
public class ProdottoController {

	private final ProdottoService service;
	private final ProdottoService prodottoService;
	private final MagazzinoService magazzinoService;

	public ProdottoController(ProdottoService service, ProdottoService prodottoService,
		MagazzinoService magazzinoService) {
		this.service = service;
		this.prodottoService = prodottoService;
		this.magazzinoService = magazzinoService;
	}

	@PostMapping
	public String inserisciProdotto(@RequestBody ProdottoDto prodottoDto, Model model) {
		service.inserisci(prodottoDto);
		return listaProdotti(model);
	}

	@GetMapping("/new")
	public String newProdotto(Model model) {
		model.addAttribute("magazzinoList", magazzinoService.findAllMagazzino());
		return "prodotto/prodottoNuovo";
	}

	@GetMapping("/list")
	public String listaProdotti(Model model) {
		List<ProdottoDto> prodottoDtoList = prodottoService.findAllProdotto();
		model.addAttribute("prodottoList", prodottoDtoList);
		return "prodotto/prodottoList";
	}

	@GetMapping("/azioni/{id}")
	public String prodottoAzioni(@PathVariable Long id, Model model) {
		ProdottoDto prodottoDto = prodottoService.ricercaId(id).orElseThrow();
		model.addAttribute("prodotto", prodottoDto);
		model.addAttribute("magazzinoList", magazzinoService.findAllMagazzino());
		return "prodotto/prodottoAzioni";
	}

	@PutMapping("{id}")
	public String aggiornaProdotto(@RequestBody ProdottoDto prodottoDto, @PathVariable Long id,
		Model model) {
		prodottoDto.setIdProdotto(id);
		service.aggiorna(prodottoDto);
		return listaProdotti(model);
	}

	@DeleteMapping("{id}")
	public String eliminaProdotto(@PathVariable Long id, Model model) {
		service.elimina(id);
		return listaProdotti(model);
	}

	@GetMapping("{id}")
	public Optional<ProdottoDto> ricercaId(@PathVariable Long id) {
		return service.ricercaId(id);
	}

}
