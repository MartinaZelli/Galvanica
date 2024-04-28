package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.ProdottoDto;
import org.galvanica.service.CRUD.MagazzinoService;
import org.galvanica.service.CRUD.ProdottoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/prodotto")
public class ProdottoController {
    private final ProdottoService service;
    private final ProdottoService prodottoService;
    private final MagazzinoService magazzinoService;

    public ProdottoController(ProdottoService service,
                              ProdottoService prodottoService,
                              MagazzinoService magazzinoService) {
        this.service = service;
        this.prodottoService = prodottoService;
        this.magazzinoService = magazzinoService;
    }

    @PostMapping
    public String inserisciProdotto(@RequestBody ProdottoDto prodottoDto,
                                    Model model) {
        service.inserisci(prodottoDto);
        return listaProdotti(model);
    }

    @GetMapping("/new")
    public String newProdotto(Model model) {
        model.addAttribute("magazzinoList", magazzinoService.findAllMagazzino());
        return "prodottoNuovo";
    }


    @PutMapping("{id}")
    public String aggiornaProdotto(@RequestBody ProdottoDto prodottoDto,
                                   @PathVariable Long id, Model model) {
        service.aggiorna(prodottoDto, id);
        return listaProdotti(model);
    }

    @GetMapping("{id}")
    public Optional<ProdottoDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaProdotto(@PathVariable Long id) {
        service.elimina(id);
    }

    @GetMapping("/list")
    public String listaProdotti(Model model) {
        List<ProdottoDto> prodottoDtoList = prodottoService.findAllProdotto();
        model.addAttribute("prodottoList", prodottoDtoList);
        return "prodottoList";
    }

    @GetMapping("/azioni/{id}")
    public String listaProdotti(@PathVariable Long id, Model model) {
        ProdottoDto prodottoDto = prodottoService.ricercaId(id).orElseThrow();
        model.addAttribute("prodotto", prodottoDto);
        model.addAttribute("magazzinoList", magazzinoService.findAllMagazzino());
        return "prodottoAzioni";
    }

}
