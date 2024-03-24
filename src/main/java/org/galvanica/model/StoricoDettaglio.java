package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;
import org.galvanica.math.UnitaDiMisura;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoricoDettaglio {

    @Id
    private Long idStoricoDettaglio;
    private Boolean eseguito;
    @ManyToOne
    private Prodotto prodotto;
    @ManyToOne
    private StoricoGenerale storicoGenerale;
    private Double quantita;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;


}
