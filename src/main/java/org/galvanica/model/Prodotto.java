package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nome"})})
public class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idProdotto;
    @ManyToOne
    private Magazzino magazzino;
    private String nome;
    private String descrizione;
    private Integer ph;
    @NonNull
    private Boolean sonoVolume;

}
