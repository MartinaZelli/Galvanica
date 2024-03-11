package org.galvanica.dto;

import lombok.*;
import org.galvanica.model.Bagno;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BagnoCaratteristicaDto {
    private Long idCaratteristica;
    private Bagno bagno;
    private String nome;
    private String descrizione;
}

