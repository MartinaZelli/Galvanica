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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"alimentazione_id_alimentazione", "prodotto_id_prodotto"})})
public class DettaglioAlimentazione {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idDettaglio;
    private String note;
    private Integer quantitaProdotto;
    @Enumerated(EnumType.STRING)
    private UnitaDiMisura unitaDiMisura;
    @ManyToOne
    private Prodotto prodotto;
    @ManyToOne
    private Alimentazione alimentazione;

}
