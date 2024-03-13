package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DettaglioAlimentazione {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idDettaglio;
    private String note;
    private Integer quantitaProdotto;
    private String unitaDiMisura;
    @ManyToOne
    private Prodotto prodotto;
    @ManyToOne
    private Alimentazione alimentazione;

}
