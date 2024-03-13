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
public class Alimentazione {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idAlimentazione;
    @ManyToOne
    private Bagno bagno;
    private Integer scatti;
    private String tempo;
    //cercare cron expression generator e dirgli che vuoi..... ???? 0 0 0 ? * MON,WED,FRI *//
    private String descrizione;
    @OneToMany(mappedBy = "alimentazione")
    private List<DettaglioAlimentazione> dettaglioAlimentazioneList;
}
