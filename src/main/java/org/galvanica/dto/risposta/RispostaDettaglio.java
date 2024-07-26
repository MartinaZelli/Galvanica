package org.galvanica.dto.risposta;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@Builder
public class RispostaDettaglio {

    private Double quantitaProdotto;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;
    private String nomeProdotto;
}
