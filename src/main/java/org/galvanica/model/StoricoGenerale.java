package org.galvanica.model;

import jakarta.persistence.*;
import lombok.*;
import org.galvanica.math.TipologiaAggiunta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoricoGenerale {

	// todo: inserire scatti inseriti come parametro!!

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idStorico;
	@ManyToOne
	private Bagno bagno;

	private Integer scattiTotaliBagno;
	private Integer restoScattiBagno;
	private Integer scattiInseriti;
	@ManyToOne
	private Alimentazione alimentazione;
	@Builder.Default
	private LocalDateTime dataCreazione = LocalDateTime.now();
	private LocalDateTime dataEsecuzione;
	private LocalDate dataControlloTempo;
	@Builder.Default
	private Boolean eseguitoGenerale = false;
	@Builder.Default
	private Boolean annullatoGenerale = false;
	@Enumerated(EnumType.STRING)
	private TipologiaAggiunta tipologiaAggiunta;
	@OneToMany(mappedBy = "storicoGenerale", cascade = CascadeType.REMOVE)
	private List<StoricoDettaglio> storicoDettaglioList;
	private Double moltiplicatoreAlimentazione;
	private String note;

}
