package org.galvanica.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.galvanica.dto.AlimentazioneRispostaDto;
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
        model.addAttribute("bagnoList",
                bagnoService.findAllBagnoIfAlimentazioneScattiNotNull());
        return "operazioniDiAlimentazione/scatti";
    }


    @GetMapping("/scatti/preconto/{id}")
    @ResponseBody
    public String preconto(@PathVariable Long id, HttpServletRequest request) {
        String scattiValue = request.getParameter(String.valueOf(id));
        if (scattiValue == null || scattiValue.isEmpty()) {
            return String.format("%.2f", 0d);
        }
        double scatti = Double.parseDouble(scattiValue);

        Integer scattiAlimentazione = alimentazioneService.ricercaAlimentazioneByBagno(
                        id)
                .stream()
                .filter(alimentazione -> alimentazione.getScatti() != null)
                .findFirst()
                .orElseThrow().getScatti();
        Double moltiplicatore = (scatti / (double) scattiAlimentazione);
        return String.format("%.2f", moltiplicatore);
    }

    @PostMapping("/scatti")
    public String calcoloAlimentazione(Model model,
                                       @RequestBody Map<Long, Integer> mappaBagnoScatti) {
        List<AlimentazioneRispostaDto> alimentazioneRispostaDtoList =
                alimentazioneAScattiService.calcolaAlimentazioneList(
                        mappaBagnoScatti);
        model.addAttribute("alimentazioneRispostaList",
                alimentazioneRispostaDtoList);
        System.out.println(alimentazioneRispostaDtoList);
        return "operazioniDiAlimentazione/rispostaScatti";
    }


    @PostMapping("/confermaTutto")
    @Transactional
    public String confermaTutto(Model model,
                                @RequestParam List<Long> idDettaglioList) {
        storiciAnnullaOConcludiService.eseguiSingolaAggiuntaList(idDettaglioList);
        return "operazioniDiAlimentazione/confermaInteraAlimentazione";
    }

    @GetMapping("/rispostaScatti")
    public String rispostaScatti(Model model) {
        return "";
    }

}
