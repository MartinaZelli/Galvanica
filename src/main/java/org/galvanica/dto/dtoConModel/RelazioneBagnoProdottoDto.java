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
    private String nomeBagno;
    private Long idProdotto;
    private String nomeProdotto;
    private String note;
}
