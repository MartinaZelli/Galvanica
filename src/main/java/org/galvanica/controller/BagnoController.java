package org.galvanica.controller;

import org.galvanica.dto.AlimentazioneScattiRisposta;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.OperazioniBagnoService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/bagno")
public class BagnoController {
    private final BagnoService service;
    private final OperazioniBagnoService operazioniBagnoService;

    public BagnoController(BagnoService service,
                           OperazioniBagnoService operazioniBagnoService) {
        this.service = service;
        this.operazioniBagnoService = operazioniBagnoService;
    }

    @PostMapping
    public BagnoDto inserisciBagno(@RequestBody BagnoDto bagnoDto) {
        return service.inserisci(bagnoDto);
    }

    @PutMapping("{id}")
    public BagnoDto aggiornaBagno(@RequestBody BagnoDto bagnoDto,
                                  @PathVariable Long id) {
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

    @PutMapping("{id}/alimentazioneScatti/{scattiParziali}")
    public AlimentazioneScattiRisposta alimentazioneScatti(@PathVariable Long id,
                                                           @PathVariable Integer scattiParziali) {
        System.out.println("alimentazioneScatti()");
        System.out.println(operazioniBagnoService.calcolaAlimentazioneScatti(
                id,
                scattiParziali));
        return operazioniBagnoService.calcolaAlimentazioneScatti(id,
                scattiParziali);
    }

    @PutMapping("eseguiSingolaAggiunta/{idStoricoDettaglio}")
    public void eseguiSingolaAggiunta(@PathVariable Long idStoricoDettaglio) {
        System.out.println("dentro eseguiSingolaAggiunta");
        operazioniBagnoService.eseguiSingolaAggiunta(idStoricoDettaglio);
    }

    @PutMapping("confermaInteraAlimentazione/{idStoricoGenerale}")
    public void confermaInteraAlimentazione(@PathVariable Long idStoricoGenerale) {
        System.out.println("dentro confermaInteraAlimentazione");
        operazioniBagnoService.confermaInteraAlimentazione(idStoricoGenerale);
    }


}
