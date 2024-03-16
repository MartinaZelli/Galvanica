package org.galvanica.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DettaglioAlimentazione {
    private Long idDettaglio;
    private String note;
    private Integer quantitaProdotto;
    private String unitaDiMisura;
    private Long idProdotto;
    private Long idAlimentazione;
}
