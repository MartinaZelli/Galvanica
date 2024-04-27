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
    public MagazzinoDto aggiungiMagazzinoDto(
            @RequestBody MagazzinoDto magazzinoDto) {
        return magazzinoService.inserisci(magazzinoDto);
    }

    @GetMapping("{id}")
    public Optional<MagazzinoDto> ricercaMagazzinoId(@PathVariable Long id) {
        return magazzinoService.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaMagazzino(@PathVariable Long id) {
        magazzinoService.elimina(id);
    }

    @PutMapping("{id}")
    public String aggiornaMagazzino(@RequestBody MagazzinoDto magazzinoDto,
                                    @PathVariable Long id, Model model) {
        magazzinoService.aggiorna(magazzinoDto, id);
        return listaMagazzini(model);
    }

    @GetMapping("/elenco")
    public String listaMagazzini(Model model) {
        List<MagazzinoDto> magazzinoDtoList = magazzinoService.findAllMagazzino();
        model.addAttribute("magazzinoList", magazzinoDtoList);
        return "magazzinoList";
    }

    @GetMapping("/azioni/{id}")
    public String listaMagazzini(@PathVariable Long id, Model model) {
        MagazzinoDto magazzinoDto = magazzinoService.ricercaId(id).orElseThrow();
        model.addAttribute("magazzino", magazzinoDto);
        return "magazzinoAzioni";
    }
}
