package org.galvanica.controller;

import org.galvanica.dto.ProdottoDto;
import org.galvanica.service.CRUD.ProdottoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/prodotto")
public class ProdottoController {
    private final ProdottoService service;

    public ProdottoController(ProdottoService service) {
        this.service = service;
    }

    @PostMapping
    public ProdottoDto inserisciProdotto(@RequestBody ProdottoDto prodottoDto) {
        return service.inserisci(prodottoDto);
    }

    @PutMapping("{id}")
    public ProdottoDto aggiornaProdotto(@RequestBody ProdottoDto prodottoDto,
                                        @PathVariable Long id) {
        return service.aggiorna(prodottoDto, id);
    }

    @GetMapping("{id}")
    public Optional<ProdottoDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaProdotto(@PathVariable Long id) {
        service.elimina(id);
    }

}
