package org.galvanica.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/search")
public class FormController {

	public final List<Anafrafic>
		anagrafics =
		Arrays.asList(new Anafrafic("Mario", "Rossi"), new Anafrafic("Luigi", "Bianchi"),
			new Anafrafic("Giulia", "Verdi"), new Anafrafic("Francesco", "Neri"),
			new Anafrafic("Anna", "Gialli"), new Anafrafic("Marco", "Azzurri"),
			new Anafrafic("Paolo", "Viola"), new Anafrafic("Laura", "Marroni"),
			new Anafrafic("Stefano", "Grigi"), new Anafrafic("Claudia", "Neri"),
			new Anafrafic("Luca", "Rossi"), new Anafrafic("Elena", "Bianchi"),
			new Anafrafic("Alessandro", "Verdi"), new Anafrafic("Chiara", "Gialli"),
			new Anafrafic("Davide", "Azzurri"), new Anafrafic("Sara", "Viola"),
			new Anafrafic("Matteo", "Marroni"), new Anafrafic("Simona", "Grigi"),
			new Anafrafic("Giorgio", "Neri"), new Anafrafic("Marta", "Rossi"),
			new Anafrafic("Antonio", "Bianchi"), new Anafrafic("Silvia", "Verdi"),
			new Anafrafic("Fabio", "Gialli"), new Anafrafic("Roberta", "Azzurri"),
			new Anafrafic("Giovanni", "Viola"), new Anafrafic("Francesca", "Marroni"),
			new Anafrafic("Enrico", "Grigi"), new Anafrafic("Valentina", "Neri"),
			new Anafrafic("Giuseppe", "Rossi"), new Anafrafic("Federica", "Bianchi"),
			new Anafrafic("Riccardo", "Verdi"), new Anafrafic("Alessia", "Gialli"),
			new Anafrafic("Pietro", "Azzurri"), new Anafrafic("Monica", "Viola"),
			new Anafrafic("Leonardo", "Marroni"), new Anafrafic("Barbara", "Grigi"),
			new Anafrafic("Emanuele", "Neri"), new Anafrafic("Beatrice", "Rossi"),
			new Anafrafic("Sandro", "Bianchi"), new Anafrafic("Gabriella", "Verdi"),
			new Anafrafic("Tommaso", "Gialli"), new Anafrafic("Cristina", "Azzurri"),
			new Anafrafic("Nicola", "Viola"), new Anafrafic("Ludovica", "Marroni"),
			new Anafrafic("Roberto", "Grigi"), new Anafrafic("Grazia", "Neri"),
			new Anafrafic("Andrea", "Rossi"), new Anafrafic("Serena", "Bianchi"),
			new Anafrafic("Lorenzo", "Verdi"));

	@GetMapping("")
	public String page(Model model) {
		model.addAttribute("anagrafics", anagrafics);
		return "search/search";
	}

	@GetMapping("/filter")
	public String filter(Model model,
		@RequestParam(name = "name", required = false, defaultValue = "") String name,
		@RequestParam(name = "surname", required = false, defaultValue = "") String surname) {
		List<Anafrafic> filteredAnafrafics = anagrafics;

		if (!Objects.equals(name, "")) {
			filteredAnafrafics =
				filteredAnafrafics.stream().filter(a -> Objects.equals(a.name, name)).toList();
		}

		if (!Objects.equals(surname, "")) {
			filteredAnafrafics =
				filteredAnafrafics.stream().filter(a -> Objects.equals(a.surname, surname))
					.toList();
		}

		model.addAttribute("anagrafics", filteredAnafrafics);
		return "search/search :: data";
	}


	@AllArgsConstructor
	@Getter
	private class Anafrafic {
		private String name;
		private String surname;
	}
}
