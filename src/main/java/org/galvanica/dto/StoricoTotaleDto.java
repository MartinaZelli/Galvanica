package org.galvanica.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@AllArgsConstructor
public abstract class StoricoTotaleDto {
	// todo: verificare crud
	private Long idBagno;
	private String nomeBagno;

	private Double quantitaProdotto;
	@Enumerated(EnumType.STRING)
	private UnitaDiMisura unitaDiMisura;
	private Long idProdotto;
	private String nomeProdotto;

	private Long idAlimentazione;

	@Enumerated(EnumType.STRING)
	private TipologiaAggiunta tipologiaAggiunta;

	private String rispostaCalcoloFront;

}
