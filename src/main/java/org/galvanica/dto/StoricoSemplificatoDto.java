package org.galvanica.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class StoricoSemplificatoDto {

    private Long idStoricoGenerale;
    private Long idStoricoDettaglio;
    private Long idBagno;
    private String nomeBagno;
    private Long idProdotto;
    private String nomeProdotto;
    private Double quantita;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;
    private Boolean eseguito;
    private Boolean escluso;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataFine;
    @Enumerated(EnumType.STRING)
    private TipologiaAggiunta tipologiaAggiunta;

}
