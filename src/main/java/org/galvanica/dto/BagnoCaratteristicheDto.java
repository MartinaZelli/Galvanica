package org.galvanica.dto;

import lombok.*;
import org.galvanica.model.Bagno;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BagnoCaratteristicheDto {
    private Long idCaratteristica;
    private Bagno bagno;
    private String Nome;
    private String misura;
}
