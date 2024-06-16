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
    private Boolean eseguitoDettaglio = false;
    @Builder.Default
    private Boolean annullatoDettaglio = false;
    @ManyToOne
    private Prodotto prodotto;
    @ManyToOne
    private StoricoGenerale storicoGenerale;
    private Integer quantita;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;


}
