package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;

import java.util.List;

@Getter
@Setter
public class StoricoTotaleGroupDto extends StoricoTotaleDto {

	private List<Long> idStoricoDettaglioList;

	@Builder
	public StoricoTotaleGroupDto(List<Long> idStoricoDettaglioList, Long idBagno, String nomeBagno,
		Double quantitaProdotto, UnitaDiMisura unitaDiMisura, Long idProdotto, String nomeProdotto,
		Long idAlimentazione,

		TipologiaAggiunta tipologiaAggiunta,

		String rispostaCalcoloFront) {
		super(idBagno, nomeBagno, quantitaProdotto, unitaDiMisura, idProdotto, nomeProdotto,
			idAlimentazione, tipologiaAggiunta, rispostaCalcoloFront);

		this.idStoricoDettaglioList = idStoricoDettaglioList;
	}
}
