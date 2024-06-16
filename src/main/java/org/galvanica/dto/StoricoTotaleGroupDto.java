package org.galvanica.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.math.UnitaDiMisura;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StoricoTotaleGroupDto extends StoricoTotaleDto {

    private List<Long> idStoricoDettaglioList;

    @Builder
    public StoricoTotaleGroupDto(
            List<Long> idStoricoDettaglioList,
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
            Integer scattiTotaliBagno, Integer scattiInseriti,
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
                moltiplicatoreAlimentazione,
                restoScattiBagno,
                scattiAlimentazione,
                scattiTotaliBagno,
                scattiInseriti,
                restoScattiPrecedenti,
                dataControlloTempo,
                tipologiaAggiunta,
                eseguitoDettaglio,
                annullatoDettaglio,
                eseguitoGenerale,
                annullatoGenerale,
                noteStoricoGenerale,
                dataCreazione,
                dataEsecuzione,
                rispostaCalcoloFront);

        this.idStoricoDettaglioList = idStoricoDettaglioList;
    }
}
