package org.galvanica.controller;

import org.galvanica.dto.RelazioneBagnoProdottoDto;
import org.galvanica.service.RelazioneBagnoProdottoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/relazioneBagnoProdotto")
public class RelazioneBagnoProdottoController {
    private final RelazioneBagnoProdottoService service;

    public RelazioneBagnoProdottoController(RelazioneBagnoProdottoService service) {
        this.service = service;
    }

    @PostMapping
    public RelazioneBagnoProdottoDto inserisciRelazione(
            @RequestBody RelazioneBagnoProdottoDto relazioneBagnoProdottoDto) {
        return service.inserisci(relazioneBagnoProdottoDto);
    }

    @PutMapping("{id}")
    public RelazioneBagnoProdottoDto aggiornaRelazione(
            @RequestBody RelazioneBagnoProdottoDto relazioneBagnoProdottoDto,
            @PathVariable Long id) {
        return service.aggiorna(relazioneBagnoProdottoDto, id);
    }

    @GetMapping("{id}")
    public Optional<RelazioneBagnoProdottoDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaRelazione(@PathVariable Long id) {
        service.elimina(id);
    }
}
