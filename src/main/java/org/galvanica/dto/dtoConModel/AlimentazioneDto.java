package org.galvanica.dto.dtoConModel;

import lombok.*;
import org.galvanica.math.TipologiaAggiunta;

import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlimentazioneDto {
    private Long idAlimentazione;
    private Long idBagno;
    private String nomeBagno;
    private TipologiaAggiunta tipologiaAggiunta;
    private Integer scatti;
    private List<DayOfWeek> tempo;
    private String descrizione;
    private Boolean valoriArrotondati;

    public void setTempo(String giorno) {
        this.tempo = List.of(DayOfWeek.valueOf(giorno));
    }
}
