package org.galvanica.dto.dtoConModel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RelazioneBagnoProdottoDto {
    private Long idRelazione;
    private Long idBagno;
    private Long idProdotto;
    private String note;
}
