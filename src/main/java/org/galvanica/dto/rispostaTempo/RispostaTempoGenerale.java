package org.galvanica.dto.rispostaTempo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class RispostaTempoGenerale {
    private String nomeBagno;
    private Long idBagno;
    private String rispostaCalcoloFront;
    private LocalDate dataControlloTempo;
    private LocalDate dataControlloPrecedente;
    private Integer numeroDiAggiunteCalcolate;
    private List<RispostaTempoDettaglio> dettaglioList;
    private List<Long> idStoricoGeneraleList;
    private List<Long> idStoricoDettaglioList;
}
