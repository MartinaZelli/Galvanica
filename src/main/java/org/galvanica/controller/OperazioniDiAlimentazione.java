package org.galvanica.controller;

import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/operazioni/alimentazioni")
public class OperazioniDiAlimentazione {
    private final BagnoService bagnoService;
    private final AlimentazioneService alimentazioneService;

    public OperazioniDiAlimentazione(BagnoService bagnoService,
                                     AlimentazioneService alimentazioneService) {
        this.bagnoService = bagnoService;
        this.alimentazioneService = alimentazioneService;
    }

    @GetMapping("/scatti")
    public String scatti(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagno());
        return "operazioniDiAlimentazione/scatti";
    }

    @GetMapping("/scatti/preconto/{id}")
    @ResponseBody
    public String preconto(@PathVariable Long id, @RequestParam Integer scatti) {
        Integer scattiAlimentazione = alimentazioneService.ricercaAlimentazioneByBagno(
                        id)
                .stream()
                .filter(alimentazione -> alimentazione.getScatti() != null)
                .findFirst()
                .orElseThrow().getScatti();
        Double moltiplicatore = ((double) scatti / (double) scattiAlimentazione);
        return String.format("%.2f", moltiplicatore);
    }
}
