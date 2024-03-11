package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Prodotto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idProdotto;
    @ManyToOne
    private Magazzino magazzino;
    private String nome;
    private String descrizione;
    private Integer ph;

}
