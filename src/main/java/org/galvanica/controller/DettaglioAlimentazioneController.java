package org.galvanica.controller;

import org.galvanica.dto.DettaglioAlimentazioneDto;
import org.galvanica.service.DettaglioAlimentazioneService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/dettaglioAlimentazione")
public class DettaglioAlimentazioneController {
    private final DettaglioAlimentazioneService service;

    public DettaglioAlimentazioneController(DettaglioAlimentazioneService service) {
        this.service = service;
    }


    @PostMapping
    public DettaglioAlimentazioneDto inserisciDettaglioAlimentazione(
            @RequestBody DettaglioAlimentazioneDto alimentazioneDto) {
        return service.inserisci(alimentazioneDto);
    }

    @PutMapping("{id}")
    public DettaglioAlimentazioneDto aggiornaDettaglioAlimentazione(
            @RequestBody DettaglioAlimentazioneDto alimentazioneDto,
            @PathVariable Long id) {
        return service.aggiorna(alimentazioneDto, id);
    }

    @GetMapping("{id}")
    public Optional<DettaglioAlimentazioneDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaDettaglioAlimentazione(@PathVariable Long id) {
        service.elimina(id);
    }
}
