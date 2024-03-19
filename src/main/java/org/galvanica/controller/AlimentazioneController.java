package org.galvanica.controller;

import org.galvanica.dto.AlimentazioneDto;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/alimentazione")
public class AlimentazioneController {
    private final AlimentazioneService service;

    public AlimentazioneController(AlimentazioneService service) {
        this.service = service;
    }

    @PostMapping
    public AlimentazioneDto inserisciAlimentazione(
            @RequestBody AlimentazioneDto alimentazioneDto) {
        return service.inserisci(alimentazioneDto);
    }

    @PutMapping("{id}")
    public AlimentazioneDto aggiornaAlimentazione(
            @RequestBody AlimentazioneDto alimentazioneDto,
            @PathVariable Long id) {
        return service.aggiorna(alimentazioneDto, id);
    }

    @GetMapping("{id}")
    public Optional<AlimentazioneDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaAlimentazione(@PathVariable Long id) {
        service.elimina(id);
    }
}
