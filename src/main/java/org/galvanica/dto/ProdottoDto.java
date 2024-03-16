package org.galvanica.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProdottoDto {
    private Long idProdotto;
    private Long idMagazzino;
    private String nome;
    private String descrizione;
    private Integer ph;
}
