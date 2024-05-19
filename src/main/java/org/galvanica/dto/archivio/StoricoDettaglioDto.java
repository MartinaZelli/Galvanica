package org.galvanica.dto.archivio;

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
    private Long idDettaglio;
    private Long idProdotto;
    private String nomeProdotto;
    private Integer quantita;
    private UnitaDiMisura unitaDiMisura;
    private Boolean eseguito;
    private Boolean escluso;
}
