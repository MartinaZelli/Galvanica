package org.galvanica.controller.calcola.alimentazione.scatti;

import jakarta.servlet.http.HttpServletRequest;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/calcola-alimentazione/scatti")
public class AlimentazioneScattiController {


	private final BagnoService bagnoService;
	private final AlimentazioneService alimentazioneService;

	public AlimentazioneScattiController(BagnoService bagnoService,
		AlimentazioneService alimentazioneService) {
		this.bagnoService = bagnoService;
		this.alimentazioneService = alimentazioneService;
	}

	@GetMapping
	public String scatti(Model model) {
		model.addAttribute("bagnoList", bagnoService.findAllBagnoIfAlimentazioneScattiNotNull());
		return "calcola-alimentazione/scatti/lista-bagni";
	}

	@GetMapping("calcola-moltiplicatore")
	@ResponseBody
	public String calcolaMoltiplicatore(@RequestParam Long idBagno, HttpServletRequest request) {
		String scattiValue = request.getParameter(String.valueOf(idBagno));
		if (scattiValue == null || scattiValue.isEmpty()) {
			return String.format("%.2f", 0d);
		}
		int scatti = Integer.parseInt(scattiValue);

		return String.format("%.2f", alimentazioneService.calcolaMoltiplicatore(idBagno, scatti));
	}


}
