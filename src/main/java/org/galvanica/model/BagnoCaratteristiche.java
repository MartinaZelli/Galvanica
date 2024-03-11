package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BagnoCaratteristiche {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idCaratteristica;
    @ManyToOne
    private Bagno bagno;
    private String nome;
    private String misura;
}
