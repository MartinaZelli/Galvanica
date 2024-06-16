package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class StoricoTotaleSingoloDto extends StoricoTotaleDto {

    private Long idStoricoDettaglio;
    private Long idStoricoGenerale;
}
