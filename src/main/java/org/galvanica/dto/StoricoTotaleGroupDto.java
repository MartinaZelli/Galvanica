package org.galvanica.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class StoricoTotaleGroupDto extends StoricoTotaleDto {

    private List<Long> idStoricoDettaglioList;
}
