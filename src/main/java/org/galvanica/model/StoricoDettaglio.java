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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idStoricoDettaglio;
    @Builder.Default
    private Boolean eseguito = false;
    @Builder.Default
    private Boolean escluso = false;
    @ManyToOne
    private Prodotto prodotto;
    @ManyToOne
    private StoricoGenerale storicoGenerale;
    private Double quantita;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;


}
