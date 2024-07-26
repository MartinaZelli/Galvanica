package org.galvanica.dto.risposta.tempo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.dto.risposta.RispostaDettaglio;

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
    private List<RispostaDettaglio> dettaglioList;
    private List<Long> idStoricoGeneraleList;
    private List<Long> idStoricoDettaglioList;
}
