package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.RelazioneBagnoProdottoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.CRUD.ProdottoService;
import org.galvanica.service.CRUD.RelazioneBagnoProdottoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/relazioneBagnoProdotto")
public class RelazioneBagnoProdottoController {
    private final RelazioneBagnoProdottoService relazioneService;
    private final BagnoService bagnoService;
    private final ProdottoService prodottoService;

    public RelazioneBagnoProdottoController(
            RelazioneBagnoProdottoService relazioneService,
            BagnoService bagnoService,
            ProdottoService prodottoService) {
        this.relazioneService = relazioneService;
        this.bagnoService = bagnoService;
        this.prodottoService = prodottoService;
    }

    @PostMapping
    public String inserisciRelazione(
            @RequestBody RelazioneBagnoProdottoDto relazioneBagnoProdottoDto,
            Model model) {

        return relazioneVistaAzioni(relazioneService.inserisci(
                relazioneBagnoProdottoDto).getIdBagno(), model);

    }

    @GetMapping("/new/{id}")
    public String newRelazione(@PathVariable Long id, Model model) {
        model.addAttribute("prodottoList", prodottoService.findAllProdotto());
        model.addAttribute("bagno", bagnoService.ricercaId(id).orElseThrow());
        return "relazioneBagnoProdotto/relazioneNuovo";
    }

    @GetMapping("/list")
    public String listaRelazione(Model model) {
        model.addAttribute("bagnoList", bagnoService.findAllBagno());
        return "relazioneBagnoProdotto/relazioneList";
    }

    @GetMapping("/vistaAzioni/{id}")
    public String relazioneVistaAzioni(@PathVariable Long id, Model model) {
        model.addAttribute("bagno", bagnoService.ricercaId(id).orElseThrow());
        model.addAttribute("relazioneList",
                relazioneService.findAllRelazionePerBagno(id));
        return "relazioneBagnoProdotto/relazioneVistaAzioni";
    }

    @GetMapping("/azioni/{id}")
    public String relazioneAzioni(@PathVariable Long id,
                                  Model model) {
        model.addAttribute("prodottoList", prodottoService.findAllProdotto());
        model.addAttribute("relazione",
                relazioneService.ricercaId(id).orElseThrow());
        return "relazioneBagnoProdotto/relazioneAzioni";
    }

    @PutMapping("{id}")
    public String aggiornaRelazione(
            @RequestBody RelazioneBagnoProdottoDto relazioneBagnoProdottoDto,
            @PathVariable Long id, Model model) {
        relazioneBagnoProdottoDto.setIdRelazione(id);
        return relazioneVistaAzioni(relazioneService.aggiorna(
                relazioneBagnoProdottoDto).getIdBagno(), model);
    }

    @DeleteMapping("{id}")
    public String eliminaRelazione(@PathVariable Long id, Model model) {
        relazioneService.elimina(id);
        return listaRelazione(model);
    }

    @GetMapping("/prodottoList/{id}")
    public String prodottoList(@PathVariable Long id, Model model) {
        model.addAttribute("prodottoList",
                prodottoService.ricercaProdottiByBagno(id));
        return "relazioneBagnoProdotto/relazioniProdottoList :: prodottoList";
    }

    @GetMapping("{id}")
    public Optional<RelazioneBagnoProdottoDto> ricercaId(@PathVariable Long id) {
        return relazioneService.ricercaId(id);
    }
}


