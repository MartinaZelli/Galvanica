package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller()
public class FrontEndTestController {

    private final BagnoService bagnoService;

    public FrontEndTestController(BagnoService bagnoService) {
        this.bagnoService = bagnoService;
    }


    @GetMapping("bagnoFe/elenco")
    public String listaBagni(Model model) {
        List<BagnoDto> bagni = bagnoService.getAllBagno();
        model.addAttribute("bagni", bagni);
        return "bagnoList";
    }

    @GetMapping("bagnoFe/edit/{id}")
    public String listaBagni(@PathVariable Long id, Model model) {
        BagnoDto bagno = bagnoService.ricercaId(id).orElseThrow(RuntimeException::new);
        model.addAttribute("bagno", bagno);
        return "bagnoCard :: editCard";
    }

    @PutMapping(value = "bagnoFe/{id}")
    public String aggiornaBagno(@RequestBody BagnoDto bagnoDto, @PathVariable Long id, Model model) {
        BagnoDto bagno = bagnoService.aggiorna(bagnoDto, id);
        model.addAttribute("bagno", bagno);
        return "bagnoCard :: showCard";
    }

}
