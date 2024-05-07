package org.galvanica.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.AlimentazioneSingolaDto;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.OperazioniAddStorico;
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
        List<AlimentazioneRisposta> alimentazioneRispostaList =
                operazioniAddStoricoService.scattiCalcolaAlimentazioneList(
                        mappaBagnoScatti);
        System.out.println(alimentazioneRispostaList);
        return scatti(model);
    }

    @PostMapping("/scatti/singola")
    public String calcoloAlimentazioneSingola(
            @RequestBody AlimentazioneSingolaDto alimentazione,
            Model model) {
        operazioniAddStoricoService.scattiCalcolaAlimentazione(
                alimentazione.getIdBagno(),
                alimentazione.getScatti());
        return scattiSingola(model);
    }


    @GetMapping("/scattiSingola")
    public String scattiSingola(Model model) {
        model.addAttribute("bagnoList",
                bagnoService.findAllBagnoIfAlimentazioneScattiNotNull());
        return "operazioniDiAlimentazione/scattiSingola";
    }


}
