package org.galvanica.controller;

import org.galvanica.dto.CaratteristicaBagnoDto;
import org.galvanica.service.BagnoService;
import org.galvanica.service.CaratteristicaBagnoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/caratteristicaBagno")
public class CaratteristicaBagnoController {
    private final BagnoService bagnoService;
    private final CaratteristicaBagnoService caratteristicaBagnoService;

    public CaratteristicaBagnoController(BagnoService bagnoService, CaratteristicaBagnoService caratteristicaBagnoService) {
        this.bagnoService = bagnoService;
        this.caratteristicaBagnoService = caratteristicaBagnoService;
    }

    @PostMapping
    public CaratteristicaBagnoDto inserisciCaratteristicaBagno(@RequestBody CaratteristicaBagnoDto caratteristicaBagnoDto) {
        return caratteristicaBagnoService.inserisci(caratteristicaBagnoDto);
    }

    @PutMapping("{id}")
    public CaratteristicaBagnoDto aggiornaCaratteristicaBagno(@RequestBody CaratteristicaBagnoDto caratteristicaBagnoDto, @PathVariable Long id) {
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
