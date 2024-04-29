package org.galvanica.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.galvanica.dto.AlimentazioneRisposta;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.OperazioniAddStorico;
import org.galvanica.service.operazioniBagno.OperazioniInStorico;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bagno")
public class BagnoController {

    private final BagnoService service;
    private final OperazioniAddStorico operazioniAddStorico;
    private final OperazioniInStorico operazioniInStorico;

    private static final Long LIMITE_LISTA = 10L;

    public BagnoController(BagnoService service,
            OperazioniAddStorico operazioniAddStorico,
            OperazioniInStorico operazioniInStorico) {
        this.service = service;
        this.operazioniAddStorico = operazioniAddStorico;
        this.operazioniInStorico = operazioniInStorico;
    }

    @PostMapping
    public String inserisciBagno(@RequestBody BagnoDto bagnoDto, Model model) {
        service.inserisci(bagnoDto);
        return listaBagni(model);
    }

    @GetMapping("/new")
    public String newBagno(Model model) {
        return "bagno/bagnoNuovo";
    }

    @GetMapping("/list")
    public String listaBagni(Model model) {
        List<BagnoDto> bagnoDtoList = service.findAllBagno();
        model.addAttribute("bagnoList", bagnoDtoList);
        return "bagno/bagnoList";
    }

    @PutMapping("{id}")
    public String aggiornaBagno(@RequestBody BagnoDto bagnoDto,
            @PathVariable Long id, Model model) {
        service.aggiorna(bagnoDto, id);
        return listaBagni(model);
    }

    @GetMapping("/azioni/{id}")
    public String listaBagni(@PathVariable Long id, Model model) {
        BagnoDto bagnoDto = service.ricercaId(id).orElseThrow();
        model.addAttribute("bagno", bagnoDto);
        return "bagno/bagnoAzioni";
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
        operazioniInStorico.eseguiSingolaAggiunta(idStoricoDettaglio);
    }

    @PutMapping("confermaInteraAlimentazione/{idStoricoGenerale}")
    public void confermaInteraAlimentazione(@PathVariable Long idStoricoGenerale) {
        System.out.println("dentro confermaInteraAlimentazione");
        operazioniInStorico.confermaInteraAlimentazione(idStoricoGenerale);
    }

    @GetMapping("vedi/{id}")
    public String vediBagno(@PathVariable Long id, Model model) {
        model.addAttribute("bagno", service.modelRicercaId(id));
        return "bagnoModifica :: bagnoVedi";
    }

    @GetMapping("{id}")
    public String bagnoGenerale(@PathVariable Long id, Model model) {
        model.addAttribute("bagno", service.modelRicercaId(id));
        model.addAttribute("storicoList",
                service.storicoGeneraleListByBagno(id, LIMITE_LISTA));
        return "bagnoGenerale";
    }
}
