package org.galvanica.dto.dtoConModel;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlimentazioneDto {
    private Long idAlimentazione;
    private Long idBagno;
    private String nomeBagno;
    private String tipoAlimentazione;
    private Integer scatti;
    private List<String> tempo;
    private String descrizione;
    private Boolean arrotondaValori = false;
}
