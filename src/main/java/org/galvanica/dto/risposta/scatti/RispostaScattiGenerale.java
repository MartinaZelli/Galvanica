package org.galvanica.dto.risposta.scatti;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.dto.risposta.RispostaDettaglio;

import java.util.List;

@Getter
@Setter
@Builder
public class RispostaScattiGenerale {
    private String nomeBagno;
    private String rispostaCalcoloFront;//
    private Long idStoricoGenerale;
    private Double moltiplicatoreAlimentazione;
    private Integer restoScattiBagno;
    private Integer scattiAlimentazione;
    private Integer scattiTotaliBagno;
    private Integer scattiInseriti;
    private Integer restoScattiPrecedenti;//
    private List<RispostaDettaglio> dettaglioList;

}
