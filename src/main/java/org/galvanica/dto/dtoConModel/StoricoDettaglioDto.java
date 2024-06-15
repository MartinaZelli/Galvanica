package org.galvanica.dto.dtoConModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@Builder
@ToString
public class StoricoDettaglioDto {
    private Long idStoricoGenerale;
    private Long idStoricoDettaglio;
    private Long idProdotto;
    private String nomeProdotto;
    private Double quantita;
    private UnitaDiMisura unitaDiMisura;
    private Boolean eseguito;
    private Boolean escluso;
}
