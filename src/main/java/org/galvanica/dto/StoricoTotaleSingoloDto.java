package org.galvanica.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
public class StoricoTotaleSingoloDto extends StoricoTotaleDto implements Cloneable {

    private Long idStoricoDettaglio;
    private Long idStoricoGenerale;

    private Double moltiplicatoreAlimentazione;
    private Integer restoScattiBagno;
    private Integer scattiAlimentazione;
    private Integer scattiTotaliBagno;
    private Integer scattiInseriti;
    private Integer restoScattiPrecedenti;
    private LocalDate dataControlloTempo;
    private Boolean eseguitoDettaglio;
    private Boolean annullatoDettaglio;
    private Boolean eseguitoGenerale;
    private Boolean annullatoGenerale;
    private String noteStoricoGenerale;

    private LocalDateTime dataCreazione;
    private LocalDateTime dataEsecuzione;


    @Override
    public StoricoTotaleSingoloDto clone() {
        try {
            StoricoTotaleSingoloDto clone = (StoricoTotaleSingoloDto) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the
            // original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
