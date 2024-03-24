package org.galvanica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    private Long idStorico;
    @ManyToOne
    private Bagno bagno;
    private Integer scattiTotali;
    private Integer restoScatti;
    @ManyToOne
    private Alimentazione alimentazione;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataFine;
    private Boolean concluso;
    @OneToMany
    private List<StoricoDettaglio> storicoDettaglio;
    private Double moltiplicatoreAlimentazione;

}
