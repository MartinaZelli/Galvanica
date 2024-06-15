package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.DettaglioAlimentazioneDto;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.DettaglioAlimentazioneService;
import org.galvanica.service.CRUD.ProdottoService;
import org.galvanica.service.mathService.MathService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/dettaglioAlimentazione")
public class DettaglioAlimentazioneController {
    private final DettaglioAlimentazioneService dettaglioAlimentazioneService;
    private final AlimentazioneService alimentazioneService;
    private final ProdottoService prodottoService;
    private final MathService mathService;

    public DettaglioAlimentazioneController(
            DettaglioAlimentazioneService dettaglioAlimentazioneService,
            AlimentazioneService alimentazioneService,
            ProdottoService prodottoService, MathService mathService) {
        this.dettaglioAlimentazioneService = dettaglioAlimentazioneService;
        this.alimentazioneService = alimentazioneService;
        this.prodottoService = prodottoService;
        this.mathService = mathService;
    }

    @GetMapping("/list")
    public String listaDettaglioAlimentazione(Model model) {
        model.addAttribute("alimentazioneList",
                alimentazioneService.findAllAlimentazione());
        return "dettaglioAlimentazione/dettaglioAlimentazioneList";
    }

    @GetMapping("/dettaglioAlimentazioneList/{id}")
    public String dettaglioAlimentazioneList(@PathVariable Long id, Model model) {
        model.addAttribute("dettaglioAlimentazioneList",
                dettaglioAlimentazioneService.ricercaDettaglioAlimentazioneByAlimentazione(
                        id));
        return "dettaglioAlimentazione/dettaglioAlimentazioneFragment :: dettaglioAlimentazioneFragment";
    }

    @RequestMapping(value = "/vistaAzioni/{id}", method = {RequestMethod.GET, RequestMethod.PUT})
    public String dettaglioAlimentazioneVistaAzioni(@PathVariable Long id,
                                                    Model model) {
        model.addAttribute("alimentazione",
                alimentazioneService.ricercaId(id).orElseThrow());
        model.addAttribute("dettaglioAlimentazioneList",
                dettaglioAlimentazioneService.ricercaDettaglioAlimentazioneByAlimentazione(
                        id));
        return "dettaglioAlimentazione/dettaglioAlimentazioneVistaAzioni";
    }

    @DeleteMapping("{id}")
    public String eliminaDettaglioAlimentazione(@PathVariable Long id, Model model) {
        Long idRedirect = dettaglioAlimentazioneService.ricercaId(id)
                .orElseThrow()
                .getIdAlimentazione();
        dettaglioAlimentazioneService.elimina(id);
        return dettaglioAlimentazioneVistaAzioni(idRedirect, model);
    }

    @GetMapping("/azioni/{id}")
    public String relazioneAzioni(@PathVariable Long id, Model model) {
        model.addAttribute("dettaglioAlimentazione",
                dettaglioAlimentazioneService.ricercaId(id).orElseThrow());
        model.addAttribute("prodottoList",
                prodottoService.ricercaProdottiByDettaglioAlimentazione(id));
        model.addAttribute("unitaDiMisuraList", mathService.unitaDiMisuraList());

        return "dettaglioAlimentazione/dettaglioAlimentazioneAzioni";
    }

    @PutMapping("{id}")
    public String aggiornaDettaglioAlimentazione(
            @RequestBody DettaglioAlimentazioneDto dettaglioAlimentazioneDto,
            @PathVariable Long id) {
        dettaglioAlimentazioneDto.setIdAlimentazione(id);
        DettaglioAlimentazioneDto dettaglio = dettaglioAlimentazioneService.aggiorna(
                dettaglioAlimentazioneDto);
        return "redirect:/dettaglioAlimentazione/vistaAzioni/" + dettaglio.getIdAlimentazione();
    }

    @GetMapping("/new/{id}")
    public String newDettaglioAlimentazione(@PathVariable Long id, Model model) {
        model.addAttribute("prodottoList",
                prodottoService.ricercaProdottiByAlimentazione(id));
        model.addAttribute("unitaDiMisuraList", mathService.unitaDiMisuraList());
        model.addAttribute("alimentazione",
                alimentazioneService.ricercaId(id).orElseThrow());
        return "dettaglioAlimentazione/dettaglioAlimentazioneNuovo";
    }

    @PostMapping
    public String inserisciDettaglioAlimentazione(
            @RequestBody DettaglioAlimentazioneDto dettaglioAlimentazioneDto) {
        DettaglioAlimentazioneDto dettaglio = dettaglioAlimentazioneService.inserisci(
                dettaglioAlimentazioneDto);
        return "redirect:/dettaglioAlimentazione/vistaAzioni/" + dettaglio.getIdAlimentazione();
    }


    @GetMapping("{id}")
    public Optional<DettaglioAlimentazioneDto> ricercaId(@PathVariable Long id) {
        return dettaglioAlimentazioneService.ricercaId(id);
    }


}
