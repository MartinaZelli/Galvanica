package org.galvanica.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@AllArgsConstructor
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
