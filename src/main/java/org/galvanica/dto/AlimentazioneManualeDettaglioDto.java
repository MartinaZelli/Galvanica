package org.galvanica.dto;

import lombok.*;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class AlimentazioneManualeDettaglioDto {

	private Long idStoricoDettaglio;
	private Boolean eseguitoDettaglio;
	private Boolean annullatoDettaglio;
	private String nomeProdotto;
	private Long idProdotto;
	private Long idStoricoGenerale;
	private Double quantita;
	private UnitaDiMisura unitaDiMisura;

}
