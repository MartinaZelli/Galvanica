package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoricoGenerale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idStorico;
    @ManyToOne
    private Bagno bagno;
    private Integer scattiTotali;
    private Integer restoScatti;
    @ManyToOne
    private Alimentazione alimentazione;
    private LocalDateTime dataCreazione = LocalDateTime.now();
    private LocalDateTime dataFine;
    private Boolean concluso = false;
    @OneToMany(mappedBy = "storicoGenerale")
    private List<StoricoDettaglio> storicoDettaglio;
    private Double moltiplicatoreAlimentazione;

}
