package org.galvanica.dto.dtoConModel;

import lombok.*;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DettaglioAlimentazioneDto {
    private Long idDettaglio;
    private String note;
    private Double quantitaProdotto;
    private UnitaDiMisura unitaDiMisura;
    private Long idProdotto;
    private Long idAlimentazione;
}
