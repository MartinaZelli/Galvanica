package org.galvanica.controller.calcola.alimentazione.tempo;

import lombok.extern.slf4j.Slf4j;
import org.galvanica.dto.alimentazione.tempo.DataControlloRequestDto;
import org.galvanica.dto.risposta.tempo.RispostaTempoGenerale;
import org.galvanica.service.operazioniBagno.AlimentazioneATempoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/calcola-alimentazione/tempo")
@Slf4j
public class AlimentazioneTempoController {


	private final AlimentazioneATempoService alimentazioneATempoServiceService;



	public AlimentazioneTempoController(AlimentazioneATempoService alimentazioneATempoServiceService) {
		this.alimentazioneATempoServiceService = alimentazioneATempoServiceService;
	}

	@GetMapping
	public String primaPagina() {
		return "calcola-alimentazione/tempo/prima-pagina";
	}

	@PostMapping
	public String calcolaAlimentazioni(Model model, @RequestBody DataControlloRequestDto dataControlloRequestDto) {


		List<RispostaTempoGenerale> rispostaATempoList =
			alimentazioneATempoServiceService.calcolaRispostaList(dataControlloRequestDto.getDataControllo());

		model.addAttribute("rispostaATempoList", rispostaATempoList);

		return "calcola-alimentazione/tempo/fragments :: bagnoCard";
	}


}
