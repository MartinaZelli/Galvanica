package org.galvanica.dto.dtoConModel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BagnoDto {

    private Long idBagno;
    private String nome;
    private Integer scattiTotali;
    private Integer restoScatti;
    private Integer litri;
}