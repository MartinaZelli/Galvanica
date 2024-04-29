package org.galvanica.controller;

import java.util.Optional;

import org.galvanica.dto.dtoConModel.CaratteristicaBagnoDto;
import org.galvanica.service.CRUD.CaratteristicaBagnoService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caratteristicaBagno")
public class CaratteristicaBagnoController {
    private final CaratteristicaBagnoService caratteristicaBagnoService;

    public CaratteristicaBagnoController(CaratteristicaBagnoService caratteristicaBagnoService) {
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
        return caratteristicaBagnoService.aggiorna(caratteristicaBagnoDto, id);
    }

    @GetMapping("{id}")
    public Optional<CaratteristicaBagnoDto> ricercaCaratteristicaPerId(@PathVariable Long id) {
        return caratteristicaBagnoService.ricercaId(id);
    }

    @DeleteMapping("{id}")
    public void eliminaCaratteristicaBagno(@PathVariable Long id) {
        caratteristicaBagnoService.elimina(id);
    }
}
