package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;
import org.galvanica.math.UnitaDiMisura;
import org.springframework.lang.NonNull;

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
    @NonNull
    private UnitaDiMisura unitaDiMisura;
    @ManyToOne
    @NonNull
    private Prodotto prodotto;
    @ManyToOne
    @NonNull
    private Alimentazione alimentazione;

}
