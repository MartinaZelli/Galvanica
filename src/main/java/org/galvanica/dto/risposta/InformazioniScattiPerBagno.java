package org.galvanica.dto.risposta;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InformazioniScattiPerBagno {
    private Long idBagno;
    private Integer scattiTotaliBagno;
    private Integer restoScattiBagno;
}
