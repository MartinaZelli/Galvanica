package org.galvanica.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BagnoCaratteristicaDto {
    private Long idCaratteristica;
    private Long idBagno;
    private String nome;
    private String descrizione;
}

