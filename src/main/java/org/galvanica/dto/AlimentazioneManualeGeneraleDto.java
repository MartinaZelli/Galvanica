package org.galvanica.dto;

import lombok.*;
import org.galvanica.math.TipologiaAggiunta;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class AlimentazioneManualeGeneraleDto {

	private Long idStorico;

	private Long idBagno;
	private String nomeBagno;
	private Integer scattiTotaliBagno;
	private Integer restoScattiBagno;
	private Integer scattiInseriti;
	private Boolean eseguitoGenerale;
	private Boolean annullatoGenerale;
	private TipologiaAggiunta tipologiaAggiunta;
	private List<AlimentazioneManualeDettaglioDto> dettaglioList;
	private String note;

}
