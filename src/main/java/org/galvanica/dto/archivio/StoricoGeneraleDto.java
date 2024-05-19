package org.galvanica.dto.archivio;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class StoricoGeneraleDto {

    private LocalDateTime dataCreazione;
    private LocalDateTime dataFine;
    private Boolean concluso;
    private Boolean annullato;
    private Integer scattiInseriti;
    private LocalDate dataControllo;
    private Boolean sonoScatti;
    private String nomeBagno;
    private Long idBagno;
    private Long idStoricoGenerale;


}
