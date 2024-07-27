package org.galvanica.controller.alimentazione;

import jakarta.transaction.Transactional;
import org.galvanica.dto.risposta.scatti.RispostaScattiGenerale;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.AlimentazioneAScattiService;
import org.galvanica.service.operazioniBagno.StoriciAnnullaOConcludiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller

@RequestMapping("/operazioni/alimentazioni")
public class OperazioniDiAlimentazione {

    private final BagnoService bagnoService;
    private final AlimentazioneService alimentazioneService;
    private final StoriciAnnullaOConcludiService storiciAnnullaOConcludiService;
    private final AlimentazioneAScattiService alimentazioneAScattiService;

    public OperazioniDiAlimentazione(BagnoService bagnoService,
                                     AlimentazioneService alimentazioneService,
                                     StoriciAnnullaOConcludiService storiciAnnullaOConcludiService,
                                     AlimentazioneAScattiService alimentazioneAScattiService) {
        this.bagnoService = bagnoService;
        this.alimentazioneService = alimentazioneService;
        this.storiciAnnullaOConcludiService = storiciAnnullaOConcludiService;

        this.alimentazioneAScattiService = alimentazioneAScattiService;
    }

    @GetMapping("/scatti")
    public String scatti(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagnoIfAlimentazioneScattiNotNull());
        return "operazioniDiAlimentazione/scatti";
    }

    @PostMapping("/scatti")
    public String calcoloAlimentazione(Model model, @RequestBody Map<Long, Integer> mappaBagnoScatti) {

        List<RispostaScattiGenerale> rispostaScattiGeneraleList = alimentazioneAScattiService.calcolaRispostaList(
                mappaBagnoScatti);
        model.addAttribute("rispostaScattiGeneraleList", rispostaScattiGeneraleList);
        return "operazioniDiAlimentazione/rispostaScatti";
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
