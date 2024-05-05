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
public class AlimentazioneRisposta {

    private Long idBagno;
    private List<OggettoAggiunta> oggettoAggiuntaList;
    private Double moltiplicatoreAlimentazione;
    private Integer restoScatti;
    private String messaggio;


}
