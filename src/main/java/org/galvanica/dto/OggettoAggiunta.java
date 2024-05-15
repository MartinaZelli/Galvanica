package org.galvanica.dto;

import lombok.*;
import org.galvanica.math.UnitaDiMisura;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OggettoAggiunta {
    private Double quantitaProdotto;
    private UnitaDiMisura unitaDiMisura;
    private Long idProdotto;
    private String nomeProdotto;
    private List<Long> idStoricoDettaglioList;

}
