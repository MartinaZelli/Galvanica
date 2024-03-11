package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FrequenzaAggiunte {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idFrequenza;
    @ManyToOne
    private Bagno bagno;
    private Integer scatti;
    //todo: capire che cazzo è TEMPO! time? Date? cazzo è????
    private Integer tempo;
    private String descrizione;
}
