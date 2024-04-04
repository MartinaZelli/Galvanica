package org.galvanica.controller;

import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.OperazioniBagno.OperazioniScattiService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/bagno")
public class BagnoController {
    private final BagnoService service;
    private final OperazioniScattiService operazioniScattiService;

    public BagnoController(BagnoService service,
                           OperazioniScattiService operazioniScattiService) {
        this.service = service;
        this.operazioniScattiService = operazioniScattiService;
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
    public AlimentazioneRisposta alimentazioneScatti(@PathVariable Long id,
                                                     @PathVariable Integer scattiParziali) {
        return operazioniScattiService.calcolaAlimentazioneScatti(id,
                scattiParziali);
    }

    @PutMapping("eseguiSingolaAggiunta/{idStoricoDettaglio}")
    public void eseguiSingolaAggiunta(@PathVariable Long idStoricoDettaglio) {
        System.out.println("dentro eseguiSingolaAggiunta");
        operazioniScattiService.eseguiSingolaAggiuntaScatti(idStoricoDettaglio);
    }

    @PutMapping("confermaInteraAlimentazione/{idStoricoGenerale}")
    public void confermaInteraAlimentazione(@PathVariable Long idStoricoGenerale) {
        System.out.println("dentro confermaInteraAlimentazione");
        operazioniScattiService.confermaInteraAlimentazione(idStoricoGenerale);
    }


}
