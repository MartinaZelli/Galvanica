package org.galvanica.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public abstract class StoricoTotaleDto {
    //todo: verificare crud
    private Long idBagno;
    private String nomeBagno;

    private Double quantitaProdotto;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;
    private Long idProdotto;
    private String nomeProdotto;

    private Long idAlimentazione;

    private Double moltiplicatoreAlimentazione;
    private Integer restoScattiBagno;
    private Integer scattiAlimentazione;
    private Integer scattiTotaliBagno;
    private Integer scattiInseriti;
    private Integer restoScattiPrecedenti;
    private LocalDate dataControlloTempo;

    @Enumerated(EnumType.STRING)
    private TipologiaAggiunta tipologiaAggiunta;

    private Boolean eseguitoDettaglio;
    private Boolean annullatoDettaglio;
    private Boolean eseguitoGenerale;
    private Boolean annullatoGenerale;
    private String noteStoricoGenerale;

    private LocalDateTime dataCreazione;
    private LocalDateTime dataEsecuzione;


    private String rispostaCalcoloFront;


}
