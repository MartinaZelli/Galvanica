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
public class AlimentazioneRispostaDto {

    private Long idBagno;
    private String nomeBagno;
    private List<OggettoAggiuntaDto> oggettoAggiuntaDtoList;
    private Double moltiplicatoreAlimentazione;
    private Integer restoScattiBagno;
    private Integer scattiAlimentazione;
    private Integer scattiTotaliBagno;
    private Integer scattiInseriti;
    private Integer restoScattiPrecedenti;
    private String messaggio;
    private Long idStoricoGenerale;
    private List<Long> idStoricoDettaglioList;


}
