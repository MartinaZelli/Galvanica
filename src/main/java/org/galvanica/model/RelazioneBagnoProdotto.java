package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    @ManyToMany
    private List<Prodotto> prodottoList;
}
