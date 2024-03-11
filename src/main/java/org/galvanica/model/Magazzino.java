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
public class Magazzino {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idMagazzino;
    private String descrizione;
    @OneToMany(mappedBy = "magazzino")
    private List<Prodotto> prodottiList;
}
