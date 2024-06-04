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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nome"})})
public class Bagno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idBagno;
    private String nome;
    private Integer litri;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    @OneToMany(mappedBy = "bagno", cascade = CascadeType.REMOVE)
    private List<CaratteristicaBagno> caratteristicheList;
    @OneToMany(mappedBy = "bagno", cascade = CascadeType.REMOVE)
    private List<Alimentazione> alimentazioneList;
    @OneToMany(mappedBy = "bagno", cascade = CascadeType.REMOVE)
    private List<RelazioneBagnoProdotto> relazioneBagnoProdottoList;


}
