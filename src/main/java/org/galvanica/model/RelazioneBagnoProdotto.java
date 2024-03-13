package org.galvanica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RelazioneBagnoProdotto {

    @Id
    @ManyToOne
    private Bagno bagno;
    @Id
    @ManyToOne
    private Prodotto prodotto;
    private String note;
}
