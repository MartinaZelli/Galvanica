package org.galvanica.controller;

import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.AlimentazioneService;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.OperazioniAddStorico;
import org.galvanica.service.operazioniBagno.OperazioniInStorico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/bagno")
public class BagnoController {
    private final BagnoService service;
    private final OperazioniAddStorico operazioniAddStorico;
    private final OperazioniInStorico operazioniInStorico;
    private final AlimentazioneService alimentazioneService;

    private final static Long LIMITE_LISTA = 10L;

    public BagnoController(BagnoService service,
                           OperazioniAddStorico operazioniAddStorico,
                           OperazioniInStorico operazioniInStorico,
                           AlimentazioneService alimentazioneService) {
        this.service = service;
        this.operazioniAddStorico = operazioniAddStorico;
        this.operazioniInStorico = operazioniInStorico;
        this.alimentazioneService = alimentazioneService;
    }

    @GetMapping("vedi/{id}")
    public String vediBagno(@PathVariable Long id, Model model) {
        model.addAttribute("bagno",
                service.modelRicercaId(id));
        return "bagnoModifica :: bagnoVedi";
    }

    @PutMapping(value = "aggiorna/{id}")
    public String aggiornaBagno(@RequestBody BagnoDto bagnoDto,
                                @PathVariable Long id, Model model) {
        BagnoDto bagno = service.aggiorna(bagnoDto, id);
        model.addAttribute("bagno", bagno);
        return "bagnoModifica :: showCardBagno";
    }

    @GetMapping("{id}")
    public String bagnoGenerale(@PathVariable Long id, Model model) {
        model.addAttribute("bagno",
                service.modelRicercaId(id));
        model.addAttribute("storicoList",
                service.storicoGeneraleListByBagno(id, LIMITE_LISTA));
        return "bagnoGenerale";
    }

    @PostMapping
    public BagnoDto inserisciBagno(@RequestBody BagnoDto bagnoDto) {
        return service.inserisci(bagnoDto);
    }

    @PutMapping("prova/aggiorna/{id}")
    public BagnoDto aggiornaBagno(@RequestBody BagnoDto bagnoDto,
                                  @PathVariable Long id) {
        return service.aggiorna(bagnoDto, id);
    }

    @GetMapping("prova/{id}")
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
