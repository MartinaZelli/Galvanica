package org.galvanica.dto.dtoConModel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlimentazioneDto {
    private Long idAlimentazione;
    private Long idBagno;
    private Integer scatti;
    private String tempo;
    private String descrizione;
}
