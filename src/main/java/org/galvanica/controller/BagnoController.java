package org.galvanica.controller;

import org.galvanica.dto.BagnoDto;
import org.galvanica.model.Bagno;
import org.galvanica.service.BagnoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bagno")
public class BagnoController {
    private final BagnoService service;

    public BagnoController(BagnoService service) {
        this.service = service;
    }

    @PostMapping
    public BagnoDto inserisciBagno(@RequestBody BagnoDto bagnoDto) {
        Bagno bagno = service.inserisci(bagnoDto);
        return BagnoDto.builder().idBagno(bagno.getIdBagno()).nome(bagno.getNome()).litri(bagno.getLitri()).scattiTotali(bagno.getScattiTotali()).scattiParziali(bagno.getScattiParziali()).build();
    }

    @GetMapping("hello")
    public String hello() {
        System.out.println("hello!");
        return "ciao, stronzo";
    }


}
