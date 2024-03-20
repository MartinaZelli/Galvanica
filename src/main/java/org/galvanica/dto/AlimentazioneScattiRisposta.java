package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.model.Prodotto;

import java.util.Map;

@Getter
@Setter
@Builder

public class AlimentazioneScattiRisposta {

    private Long idBagno;
    private Map<Prodotto, Double> mappaAlimentazione;
    private Double moltiplicatoreAlimentazione;
    private Integer restoScatti;


}
