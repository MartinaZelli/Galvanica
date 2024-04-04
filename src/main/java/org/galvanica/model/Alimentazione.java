package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;
import org.galvanica.math.DayOfWeekConverter;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"bagno_id_bagno", "scatti"})})
public class Alimentazione {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idAlimentazione;
    @ManyToOne
    private Bagno bagno;
    private Integer scatti;
    @Builder.Default
    private Boolean arrotondaValori = false;
    @Convert(converter = DayOfWeekConverter.class)
    private List<String> tempo;
    private String descrizione;
    @OneToMany(mappedBy = "alimentazione", cascade = CascadeType.REMOVE)
    private List<DettaglioAlimentazione> dettaglioAlimentazioneList;
    @OneToMany(mappedBy = "alimentazione", cascade = CascadeType.REMOVE)
    private List<StoricoGenerale> storicoGeneraleList;

}

