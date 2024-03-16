package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RelazioneBagnoProdotto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idRelazione;
    @ManyToOne
    private Bagno bagno;
    @ManyToOne
    private Prodotto prodotto;
    private String note;
}
