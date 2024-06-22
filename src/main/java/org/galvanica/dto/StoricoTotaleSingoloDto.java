package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
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

    @Builder
    public StoricoTotaleSingoloDto(
            Long idStoricoDettaglio,
            Long idStoricoGenerale,
            Long idBagno,
            String nomeBagno,
            Double quantitaProdotto,
            UnitaDiMisura unitaDiMisura,
            Long idProdotto,
            String nomeProdotto,
            Long idAlimentazione,
            Double moltiplicatoreAlimentazione,
            Integer restoScattiBagno,
            Integer scattiAlimentazione,
            Integer scattiTotaliBagno,
            Integer scattiInseriti,
            Integer restoScattiPrecedenti,
            LocalDate dataControlloTempo,
            TipologiaAggiunta tipologiaAggiunta,
            Boolean eseguitoDettaglio,
            Boolean annullatoDettaglio,
            Boolean eseguitoGenerale,
            Boolean annullatoGenerale,
            String noteStoricoGenerale,
            LocalDateTime dataCreazione,
            LocalDateTime dataEsecuzione,
            String rispostaCalcoloFront) {
        super(idBagno,
                nomeBagno,
                quantitaProdotto,
                unitaDiMisura,
                idProdotto,
                nomeProdotto,
                idAlimentazione,
                tipologiaAggiunta,
                rispostaCalcoloFront);
        this.idStoricoDettaglio = idStoricoDettaglio;
        this.idStoricoGenerale = idStoricoGenerale;
        this.moltiplicatoreAlimentazione = moltiplicatoreAlimentazione;
        this.restoScattiBagno = restoScattiBagno;
        this.scattiAlimentazione = scattiAlimentazione;
        this.scattiTotaliBagno = scattiTotaliBagno;
        this.scattiInseriti = scattiInseriti;
        this.restoScattiPrecedenti = restoScattiPrecedenti;
        this.dataControlloTempo = dataControlloTempo;
        this.eseguitoDettaglio = eseguitoDettaglio;
        this.annullatoDettaglio = annullatoDettaglio;
        this.eseguitoGenerale = eseguitoGenerale;
        this.annullatoGenerale = annullatoGenerale;
        this.noteStoricoGenerale = noteStoricoGenerale;

        this.dataCreazione = dataCreazione;
        this.dataEsecuzione = dataEsecuzione;
    }

    @Override
    public StoricoTotaleSingoloDto clone() {
        try {
            StoricoTotaleSingoloDto clone = (StoricoTotaleSingoloDto) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
