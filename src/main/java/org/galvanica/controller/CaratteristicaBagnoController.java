package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.CaratteristicaBagnoDto;
import org.galvanica.service.CRUD.CaratteristicaBagnoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/caratteristicaBagno")
public class CaratteristicaBagnoController {
    private final CaratteristicaBagnoService caratteristicaBagnoService;

    public CaratteristicaBagnoController(
            CaratteristicaBagnoService caratteristicaBagnoService) {
        this.caratteristicaBagnoService = caratteristicaBagnoService;
    }

    @PostMapping
    public CaratteristicaBagnoDto inserisciCaratteristicaBagno(
            @RequestBody CaratteristicaBagnoDto caratteristicaBagnoDto) {
        return caratteristicaBagnoService.inserisci(caratteristicaBagnoDto);
    }

    @PutMapping("{id}")
    public CaratteristicaBagnoDto aggiornaCaratteristicaBagno(
            @RequestBody CaratteristicaBagnoDto caratteristicaBagnoDto,
            @PathVariable Long id) {
        caratteristicaBagnoDto.setIdCaratteristica(id);
        return caratteristicaBagnoService.aggiorna(caratteristicaBagnoDto);
    }

    @GetMapping("{id}")
    public Optional<CaratteristicaBagnoDto> ricercaCaratteristicaPerId(
            @PathVariable Long id) {
        return caratteristicaBagnoService.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaCaratteristicaBagno(@PathVariable Long id) {
        caratteristicaBagnoService.elimina(id);
    }
}
