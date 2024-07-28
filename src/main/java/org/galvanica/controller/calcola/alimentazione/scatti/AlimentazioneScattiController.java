package org.galvanica.controller.calcola.alimentazione.scatti;

import jakarta.servlet.http.HttpServletRequest;
import org.galvanica.dto.risposta.scatti.RispostaScattiGenerale;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.AlimentazioneAScattiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calcola-alimentazione/scatti")
public class AlimentazioneScattiController {


	private final BagnoService bagnoService;
	private final AlimentazioneService alimentazioneService;
	private final AlimentazioneAScattiService alimentazioneAScattiService;


	public AlimentazioneScattiController(BagnoService bagnoService,
		AlimentazioneService alimentazioneService,
		AlimentazioneAScattiService alimentazioneAScattiService) {
		this.bagnoService = bagnoService;
		this.alimentazioneService = alimentazioneService;
		this.alimentazioneAScattiService = alimentazioneAScattiService;
	}

	@GetMapping
	public String listaBagni(Model model) {
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

	@PostMapping
	public String calcoloAlimentazione(Model model,
		@RequestBody Map<Long, Integer> mappaBagnoScatti) {

		List<RispostaScattiGenerale> rispostaScattiGeneraleList =
			alimentazioneAScattiService.calcolaRispostaList(mappaBagnoScatti);
		model.addAttribute("rispostaScattiGeneraleList", rispostaScattiGeneraleList);
		return "calcola-alimentazione/scatti/risposta";
	}

	//operazioniDiAlimentazione/rispostaScatti

}
