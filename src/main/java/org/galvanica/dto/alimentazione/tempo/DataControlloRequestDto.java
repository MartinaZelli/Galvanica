package org.galvanica.dto.alimentazione.tempo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DataControlloRequestDto {
	private LocalDate dataControllo;
}
