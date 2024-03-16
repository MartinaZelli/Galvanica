package org.galvanica.controller;

import org.galvanica.dto.MagazzinoDto;
import org.galvanica.service.MagazzinoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/magazzino")
public class MagazzinoController {

    private final MagazzinoService magazzinoService;

    public MagazzinoController(MagazzinoService magazzinoService) {
        this.magazzinoService = magazzinoService;
    }

    @PostMapping
    public MagazzinoDto aggiungiMagazzinoDto(@RequestBody MagazzinoDto magazzinoDto) {
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
    public MagazzinoDto aggiornaMagazzino(@RequestBody MagazzinoDto magazzinoDto, @PathVariable Long id) {
        return magazzinoService.aggiorna(magazzinoDto, id);
    }
}
