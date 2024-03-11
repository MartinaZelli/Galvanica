package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Bagno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idBagno;
    private String nome;
    private Integer scattiTotali;
    private Integer restoScatti;
    private Integer litri;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    @OneToMany(mappedBy = "bagno")
    private List<BagnoCaratteristica> caratteristicheList;
    @OneToMany(mappedBy = "bagno")
    private List<FrequenzaAggiunte> frequenzaAggiunteList;

}
