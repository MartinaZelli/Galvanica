package org.galvanica.dto.dtoConModel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BagnoDto {

	private Long idBagno;
	private String nome;
	// da prendere nello storico più recente.
	private Integer scattiTotali;
	// da prendere nello storico più recente.
	private Integer restoScatti;
	private Integer litri;
}
