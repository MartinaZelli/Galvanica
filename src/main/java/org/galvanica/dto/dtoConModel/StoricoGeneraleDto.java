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
    private Integer scattiTotaliBagno;
    private Integer restoScattiBagno;
    private Integer scattiInseriti;
    private Long idAlimentazione;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataEsecuzione;
    private LocalDate dataControlloTempo;
    private Boolean eseguitoGenerale;
    private Boolean annullatoGenerale;
    private TipologiaAggiunta tipologiaAggiunta;
    private Double moltiplicatoreAlimentazione;
    private String note;

}
