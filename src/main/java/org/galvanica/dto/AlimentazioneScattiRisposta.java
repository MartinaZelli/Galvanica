package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder

public class AlimentazioneScattiRisposta {

    private Long idBagno;
    private Map<String, String> mappaAlimentazione;
    private Double moltiplicatoreAlimentazione;
    private Integer restoScatti;
    private String messaggio;


}
