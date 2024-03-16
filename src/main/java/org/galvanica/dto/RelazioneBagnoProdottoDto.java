package org.galvanica.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelazioneBagnoProdottoDto {
    private Long idBagno;
    private Long idProdotto;
    private String note;
}
