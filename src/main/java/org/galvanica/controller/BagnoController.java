package org.galvanica.controller;

import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.OperazioniBagno.OperazioniAddStorico;
import org.galvanica.service.OperazioniBagno.OperazioniInStorico;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/bagno")
public class BagnoController {
    private final BagnoService service;
    private final OperazioniAddStorico operazioniAddStorico;
    private final OperazioniInStorico operazioniInStorico;

    public BagnoController(BagnoService service,
                           OperazioniAddStorico operazioniAddStorico,
                           OperazioniInStorico operazioniInStorico) {
        this.service = service;
        this.operazioniAddStorico = operazioniAddStorico;
        this.operazioniInStorico = operazioniInStorico;
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
        return operazioniAddStorico.scattiCalcolaAlimentazione(id,
                scattiParziali);
    }

    @PutMapping("{id}/alimentazioneTempo/{dataControllo}")
    public AlimentazioneRisposta alimentazioneTempo(@PathVariable Long id,
                                                    @PathVariable LocalDate dataControllo) {
        return operazioniAddStorico.tempoCalcolaAlimentazione(id, dataControllo);
    }

    @PutMapping("eseguiSingolaAggiunta/{idStoricoDettaglio}")
    public void eseguiSingolaAggiunta(@PathVariable Long idStoricoDettaglio) {
        System.out.println("dentro eseguiSingolaAggiunta");
        operazioniInStorico.eseguiSingolaAggiunta(idStoricoDettaglio);
    }

    @PutMapping("confermaInteraAlimentazione/{idStoricoGenerale}")
    public void confermaInteraAlimentazione(@PathVariable Long idStoricoGenerale) {
        System.out.println("dentro confermaInteraAlimentazione");
        operazioniInStorico.confermaInteraAlimentazione(idStoricoGenerale);
    }


}
