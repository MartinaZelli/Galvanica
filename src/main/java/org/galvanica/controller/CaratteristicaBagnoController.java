package org.galvanica.controller;

import org.galvanica.dto.CaratteristicaBagnoDto;
import org.galvanica.service.BagnoService;
import org.galvanica.service.CaratteristicaBagnoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
