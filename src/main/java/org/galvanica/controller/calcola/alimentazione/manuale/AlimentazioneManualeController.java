package org.galvanica.controller.calcola.alimentazione.manuale;

import lombok.extern.slf4j.Slf4j;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/calcola-alimentazione/manuale")
@Slf4j
public class AlimentazioneManualeController {


	private final BagnoService bagnoService;

	public AlimentazioneManualeController(BagnoService bagnoService) {
		this.bagnoService = bagnoService;
	}

	@GetMapping
	public String primaPagina(Model model) {
		model.addAttribute("bagnoList", bagnoService.findAllBagno());
		return "calcola-alimentazione/manuale/prima-pagina";
	}

	@GetMapping("/bagno")
	public String selezionaBagno(Model model, @RequestParam int bagnoId) {
		//model.addAttribute("bagnoList", bagnoService.findAllBagno());
		System.out.println(bagnoId);
		return "calcola-alimentazione/manuale/fragments :: bagnoSelect";
	}

}
