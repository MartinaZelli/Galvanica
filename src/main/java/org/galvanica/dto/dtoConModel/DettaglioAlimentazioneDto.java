package org.galvanica.dto.dtoConModel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DettaglioAlimentazioneDto {
    private Long idDettaglio;
    private String note;
    private Double quantitaProdotto;
    private String unitaDiMisura;
    private Long idProdotto;
    private Long idAlimentazione;
}
