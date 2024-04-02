package org.galvanica.controller;

import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.service.CRUD.BagnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FrontEndTestController {

    private final BagnoService bagnoService;

    public FrontEndTestController(BagnoService bagnoService) {
        this.bagnoService = bagnoService;
    }

    @GetMapping("/elenco-bagni")
    public String test(Model model) {
        List<BagnoDto> bagni = bagnoService.getAllBagno();

        model.addAttribute("bagni", bagni);
        return "test";
    }
}
