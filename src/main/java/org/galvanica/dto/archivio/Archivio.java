package org.galvanica.dto.archivio;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Archivio {

    private StoricoDettaglioDto storicoDettaglioDto;
    private StoricoGeneraleDto storicoGeneraleDto;
}
