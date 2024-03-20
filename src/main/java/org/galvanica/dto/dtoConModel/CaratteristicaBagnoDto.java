package org.galvanica.dto.dtoConModel;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaratteristicaBagnoDto {
    private Long idCaratteristica;
    private Long idBagno;
    private String nome;
    private String descrizione;
}

