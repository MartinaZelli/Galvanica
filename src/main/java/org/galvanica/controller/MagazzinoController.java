package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.MagazzinoDto;
import org.galvanica.service.CRUD.MagazzinoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/magazzino")
public class MagazzinoController {

    private final MagazzinoService magazzinoService;

    public MagazzinoController(MagazzinoService magazzinoService) {
        this.magazzinoService = magazzinoService;
    }

    @PostMapping
    public String aggiungiMagazzinoDto(
            @RequestBody MagazzinoDto magazzinoDto, Model model) {
        magazzinoService.inserisci(magazzinoDto);
        return listaMagazzini(model);
    }

    @GetMapping("/new")
    public String newMagazzinoDto(Model model) {
        return "magazzino/magazzinoNuovo";
    }

    @GetMapping("{id}")
    public Optional<MagazzinoDto> ricercaMagazzinoId(@PathVariable Long id) {
        return magazzinoService.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public String eliminaMagazzino(@PathVariable Long id, Model model) {
        magazzinoService.elimina(id);
        return listaMagazzini(model);
    }

    @PutMapping("{id}")
    public String aggiornaMagazzino(@RequestBody MagazzinoDto magazzinoDto,
                                    @PathVariable Long id, Model model) {
        magazzinoService.aggiorna(magazzinoDto, id);
        return listaMagazzini(model);
    }

    @GetMapping("/list")
    public String listaMagazzini(Model model) {
        List<MagazzinoDto> magazzinoDtoList = magazzinoService.findAllMagazzino();
        model.addAttribute("magazzinoList", magazzinoDtoList);
        return "magazzino/magazzinoList";
    }

    @GetMapping("/azioni/{id}")
    public String listaMagazzini(@PathVariable Long id, Model model) {
        MagazzinoDto magazzinoDto = magazzinoService.ricercaId(id).orElseThrow();
        model.addAttribute("magazzino", magazzinoDto);
        return "magazzino/magazzinoAzioni";
    }
}
