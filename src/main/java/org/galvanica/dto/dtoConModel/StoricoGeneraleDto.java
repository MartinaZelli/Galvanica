package org.galvanica.dto.dtoConModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.galvanica.math.TipologiaAggiunta;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class StoricoGeneraleDto {


    private Long idStorico;
    private Long idBagno;
    private String nomeBagno;
    private Integer scattiTotali;
    private Integer restoScatti;
    private Integer scattiInseriti;
    private Long idAlimentazione;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataFine;
    private LocalDate dataControlloTempo;
    private Boolean concluso;
    private Boolean annullato;
    private TipologiaAggiunta tipologiaAggiunta;
    private Double moltiplicatoreAlimentazione;
    private String note;

}
