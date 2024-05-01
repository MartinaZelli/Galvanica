package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.RelazioneBagnoProdottoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.CRUD.RelazioneBagnoProdottoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/relazioneBagnoProdotto")
public class RelazioneBagnoProdottoController {
    private final RelazioneBagnoProdottoService service;
    private final BagnoService bagnoService;

    public RelazioneBagnoProdottoController(RelazioneBagnoProdottoService service,
                                            BagnoService bagnoService) {
        this.service = service;
        this.bagnoService = bagnoService;
    }

    @PostMapping
    public String inserisciRelazione(
            @RequestBody RelazioneBagnoProdottoDto relazioneBagnoProdottoDto,
            Model model) {
        service.inserisci(relazioneBagnoProdottoDto);
        return listaRelazione(model);
    }

    @GetMapping("/new")
    public String newRelazione(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagnoModel());
        return "relazioneBagnoProdotto/relazioneNuovo";
    }

    @GetMapping("/list")
    public String listaRelazione(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagnoModel());
        return "relazioneBagnoProdotto/relazioneList";
    }

    @GetMapping("/azioni/{id}")
    public String relazioneAzioni(@PathVariable Long id, Model model) {
        /////TODO: questo
        return "relazioneBagnoProdotto/relazioneAzioni";
    }

    @PutMapping("{id}")
    public String aggiornaRelazione(
            @RequestBody RelazioneBagnoProdottoDto relazioneBagnoProdottoDto,
            @PathVariable Long id, Model model) {
        service.aggiorna(relazioneBagnoProdottoDto, id);
        return listaRelazione(model);
    }

    @DeleteMapping("{id}")
    public String eliminaRelazione(@PathVariable Long id, Model model) {
        service.elimina(id);
        return listaRelazione(model);
    }

    @GetMapping("{id}")
    public Optional<RelazioneBagnoProdottoDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }
}


