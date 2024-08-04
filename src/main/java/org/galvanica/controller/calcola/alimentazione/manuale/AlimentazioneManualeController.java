package org.galvanica.controller.calcola.alimentazione.manuale;

import lombok.extern.slf4j.Slf4j;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.Bagno;
import org.galvanica.model.Prodotto;
import org.galvanica.model.RelazioneBagnoProdotto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.CRUD.ProdottoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/calcola-alimentazione/manuale")
@Slf4j
public class AlimentazioneManualeController {


	private final BagnoService bagnoService;
	private final ProdottoService prodottoService;

	public AlimentazioneManualeController(BagnoService bagnoService, ProdottoService prodottoService) {
		this.bagnoService = bagnoService;
		this.prodottoService = prodottoService;
	}

	@GetMapping
	public String primaPagina(Model model) {
		model.addAttribute("bagnoList", bagnoService.findAllBagno());
		return "calcola-alimentazione/manuale/prima-pagina";
	}

	@GetMapping("/bagno")
	public String selezionaBagno(Model model, @RequestParam int bagnoId) {
		Bagno bagno = bagnoService.modelRicercaId(bagnoId);
		model.addAttribute("bagno", bagno);
		return "calcola-alimentazione/manuale/fragments :: bagnoSelect";
	}

	@GetMapping("/prodotto")
	public String selezionaProdotto(Model model, @RequestParam int bagnoId) {
		Bagno bagno = bagnoService.modelRicercaId(bagnoId);
		//model.addAttribute("bagno", bagno);
		List<Prodotto> prodottoList =
			bagno.getRelazioneBagnoProdottoList().stream().map(RelazioneBagnoProdotto::getProdotto).toList();
		model.addAttribute("prodottoList", prodottoList);
		return "calcola-alimentazione/manuale/fragments :: prodottoSelect";
	}

	@GetMapping("/prodotto/unita-misura")
	public String selezionaUnitaMisura(Model model, @RequestParam int prodottoId) {
		Prodotto prodotto = prodottoService.modelRicercaId(prodottoId);
		List<UnitaDiMisura> unitaMisuraList =
			Arrays.stream(UnitaDiMisura.values()).filter(um -> um.isSonoVolume() == prodotto.getSonoVolume()).toList();
		model.addAttribute("unitaMisuraList", unitaMisuraList);

		return "calcola-alimentazione/manuale/fragments :: unitaMisura";
	}

	@PostMapping
	@ResponseBody
	public String salvaAlimentazione(Model model,
		@RequestParam Long bagnoId,
		@RequestParam List<Long> prodottoId,
		@RequestParam List<Double> quantita,
		@RequestParam List<UnitaDiMisura> unitaMisura) {

		System.out.println(bagnoId);
		System.out.println(prodottoId);
		System.out.println(quantita);
		System.out.println(unitaMisura);
		return "";
	}

	@DeleteMapping("/riga")
	@ResponseBody
	public String eliminaRiga() {
		return "";
	}

}
