package org.galvanica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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
    private Integer scattiParziali;
    private Integer litri;
}
