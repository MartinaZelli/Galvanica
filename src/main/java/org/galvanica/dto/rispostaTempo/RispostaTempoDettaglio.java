package org.galvanica.dto.rispostaTempo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@Builder

public class RispostaTempoDettaglio {
    private Double quantitaProdottoTotale;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;
    private String nomeProdotto;
}
