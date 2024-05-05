package org.galvanica.controller;

import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.OperazioniAddStorico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/operazioni/alimentazioni")
public class OperazioniDiAlimentazione {
    private final BagnoService bagnoService;
    private final AlimentazioneService alimentazioneService;
    private final OperazioniAddStorico operazioniAddStoricoService;

    public OperazioniDiAlimentazione(BagnoService bagnoService,
                                     AlimentazioneService alimentazioneService,
                                     OperazioniAddStorico operazioniAddStoricoService) {
        this.bagnoService = bagnoService;
        this.alimentazioneService = alimentazioneService;
        this.operazioniAddStoricoService = operazioniAddStoricoService;
    }

    @GetMapping("/scatti")
    public String scatti(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagno());
        return "operazioniDiAlimentazione/scatti";
    }

    @GetMapping("/scatti/preconto/{id}")
    @ResponseBody
    public String preconto(@PathVariable Long id, @RequestParam(name = "scatti{id}") Integer scatti) {
        Integer scattiAlimentazione = alimentazioneService.ricercaAlimentazioneByBagno(id)
                .stream()
                .filter(alimentazione -> alimentazione.getScatti() != null)
                .findFirst()
                .orElseThrow().getScatti();
        Double moltiplicatore = ((double) scatti / (double) scattiAlimentazione);
        return String.format("%.2f", moltiplicatore);
    }

}
