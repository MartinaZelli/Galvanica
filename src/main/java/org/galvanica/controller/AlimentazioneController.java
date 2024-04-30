package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.AlimentazioneDto;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.mathService.MathService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/alimentazione")
public class AlimentazioneController {
    private final AlimentazioneService alimentazioneService;
    private final BagnoService bagnoService;
    private final MathService mathService;

    public AlimentazioneController(AlimentazioneService alimentazioneService,
                                   BagnoService bagnoService,
                                   MathService mathService) {
        this.alimentazioneService = alimentazioneService;
        this.bagnoService = bagnoService;
        this.mathService = mathService;
    }

    @PostMapping
    public String inserisciAlimentazione(
            @RequestBody AlimentazioneDto alimentazioneDto, Model model) {
        alimentazioneService.inserisci(alimentazioneDto);
        return listaAlimentazioni(model);
    }

    @GetMapping("/new")
    public String newAlimentazione(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagno());
        model.addAttribute("giorniList", mathService.dayOfWeekList());
        return "alimentazione/alimentazioneNuovo";
    }

    @GetMapping("/list")
    public String listaAlimentazioni(Model model) {
        List<AlimentazioneDto> alimentazioneDtoList = alimentazioneService.findAllAlimentazione();
        model.addAttribute("alimentazioneList", alimentazioneDtoList);
        return "alimentazione/alimentazioneList";
    }

    @GetMapping("/azioni/{id}")
    public String alimentazioneAzioni(@PathVariable Long id, Model model) {
        AlimentazioneDto alimentazioneDto = alimentazioneService.ricercaId(id)
                .orElseThrow();
        model.addAttribute("alimentazione", alimentazioneDto);
        model.addAttribute("bagnoList", bagnoService.findAllBagno());
        model.addAttribute("giorniList", mathService.dayOfWeekList());
        return "alimentazione/alimentazioneAzioni";
    }

    @PutMapping("{id}")
    public String aggiornaAlimentazione(
            @RequestBody AlimentazioneDto alimentazioneDto,
            @PathVariable Long id, Model model) {
        alimentazioneService.aggiorna(alimentazioneDto, id);
        return listaAlimentazioni(model);
    }

    @DeleteMapping("{id}")
    public String eliminaAlimentazione(@PathVariable Long id, Model model) {
        alimentazioneService.elimina(id);
        return listaAlimentazioni(model);

    }


    @GetMapping("{id}")
    public Optional<AlimentazioneDto> ricercaId(@PathVariable Long id) {
        return alimentazioneService.ricercaId(id);
    }

    @GetMapping("/campiScatti/{id}")
    public String campiScatti(@PathVariable Long id, Model model) {
        AlimentazioneDto alimentazioneDto = alimentazioneService.ricercaId(id)
                .orElseThrow();
        model.addAttribute("alimentazione", alimentazioneDto);
        return "alimentazione/campiTipoAlimentazione :: campiScatti";
    }

    @GetMapping("/campiTempo/{id}")
    public String campiTempo(@PathVariable Long id, Model model) {
        AlimentazioneDto alimentazioneDto = alimentazioneService.ricercaId(id)
                .orElseThrow();
        model.addAttribute("alimentazione", alimentazioneDto);
        model.addAttribute("giorniList", mathService.dayOfWeekList());
        return "alimentazione/campiTipoAlimentazione :: campiTempo";
    }

}
