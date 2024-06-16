package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class StoricoTotaleGroupDto extends StoricoTotaleDto {

    private List<Long> idStoricoDettaglioList;
    private List<Long> idStoricoGeneraleList;
}
