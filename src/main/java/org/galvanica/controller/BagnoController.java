package org.galvanica.controller;

import org.galvanica.dto.BagnoDto;
import org.galvanica.service.BagnoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/bagno")
public class BagnoController {
    private final BagnoService service;

    public BagnoController(BagnoService service) {
        this.service = service;
    }

    @PostMapping
    public BagnoDto inserisciBagno(@RequestBody BagnoDto bagnoDto) {
        return service.inserisci(bagnoDto);
    }

    @PutMapping("{id}")
    public BagnoDto aggiornaBagno(@RequestBody BagnoDto bagnoDto, @PathVariable Long id) {
        return service.aggiorna(bagnoDto, id);
    }

    @GetMapping("{id}")
    public Optional<BagnoDto> ricercaId(@PathVariable Long id) {
        return service.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaBagno(@PathVariable Long id) {
        service.elimina(id);
    }

    @GetMapping("hello")
    public String hello() {
        System.out.println("hello!");
        return "ciao, stronzo";
    }


}
