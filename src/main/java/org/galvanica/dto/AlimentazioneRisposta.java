package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder

public class AlimentazioneRisposta {

    private Long idBagno;
    private List<OggettoAggiunta> oggettoAggiuntaList;
    private Double moltiplicatoreAlimentazione;
    private Integer restoScatti;
    private String messaggio;


}
