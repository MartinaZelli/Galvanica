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
    @Builder.Default
    private LocalDateTime dataCreazione = LocalDateTime.now();
    private LocalDateTime dataFine;
    @Builder.Default
    private Boolean concluso = false;
    @OneToMany(mappedBy = "storicoGenerale", cascade = CascadeType.REMOVE)
    private List<StoricoDettaglio> storicoDettaglioList;
    private Double moltiplicatoreAlimentazione;

}
