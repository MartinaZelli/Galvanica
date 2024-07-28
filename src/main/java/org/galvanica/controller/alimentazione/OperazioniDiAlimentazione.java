package org.galvanica.controller.alimentazione;

import jakarta.transaction.Transactional;
import org.galvanica.service.operazioniBagno.AlimentazioneAScattiService;
import org.galvanica.service.operazioniBagno.StoriciAnnullaOConcludiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller

@RequestMapping("/operazioni/alimentazioni")
public class OperazioniDiAlimentazione {

	private final StoriciAnnullaOConcludiService storiciAnnullaOConcludiService;
	private final AlimentazioneAScattiService alimentazioneAScattiService;

	public OperazioniDiAlimentazione(StoriciAnnullaOConcludiService storiciAnnullaOConcludiService,
		AlimentazioneAScattiService alimentazioneAScattiService) {
		this.storiciAnnullaOConcludiService = storiciAnnullaOConcludiService;
		this.alimentazioneAScattiService = alimentazioneAScattiService;
	}


	@PostMapping("/confermaTutto")
	@Transactional
	public String confermaTutto(@RequestParam List<Long> idDettaglioList) {
		storiciAnnullaOConcludiService.eseguiSingolaAggiuntaList(idDettaglioList);
		return "operazioniDiAlimentazione/confermaInteraAlimentazione";
	}

	@GetMapping("/rispostaScatti")
	public String rispostaScatti() {
		return "";
	}

}
