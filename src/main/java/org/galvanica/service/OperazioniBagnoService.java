package org.galvanica.service;

import org.galvanica.dto.AlimentazioneScattiRisposta;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.ScattiMath;
import org.galvanica.model.*;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OperazioniBagnoService {

    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;


    public OperazioniBagnoService(BagnoRepository bagnoRepository,
                                  StoricoGeneraleRepository storicoGeneraleRepository,
                                  StoricoDettaglioRepository storicoDettaglioRepository) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    /*alimentazione a scatti prende bagno e scatti parziali, legge se tutto ha un valore per non dare eccezione,
    dopodichè verifica se l'aggiunta è da fare o meno e SE NON c'è da fare aggiunta a scatti salva il bagno con i nuovi parametri;
    se invece c'è da fare l'aggiunta a scatti crea uno storico che poi andrà confermato per aggiornare il bagno.
   TODO: fare il metodo conferma storico.
     */
    public AlimentazioneScattiRisposta faiAlimentazioneScatti(Long id,
                                                              Integer scattiParziali) {
        Optional<Bagno> bagnoTrovato = bagnoRepository.findById(id);
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "metti un id corretto che questo non c'è, bischero.");
        }
        Bagno bagno = bagnoTrovato.get();
        Optional<Alimentazione> alimentazione = bagno.getAlimentazioneList()
                .stream()
                .filter(alimentazioneFilter -> alimentazioneFilter.getScatti() != null)
                .findFirst();
        if (alimentazione.isEmpty()) {
            throw new RuntimeException(
                    "se il bagno non ha alimentazione a scatti di sto numero mi ci gratto");
        }
        int scattiAttuali = bagno.getRestoScatti() + scattiParziali;
        Double primoValoreVolumetrico = alimentazione.get()
                .getDettaglioAlimentazioneList()
                .stream()
                .filter(dettaglioAlimentazione -> dettaglioAlimentazione.getUnitaDiMisura()
                        .isSonoVolume())
                .findFirst()
                .map(DettaglioAlimentazione::getQuantitaProdotto)
                .orElse(null);
        ScattiMath scattiMath = MetodiArrotondamenti.alimentazioneScattiMath(
                scattiAttuali,
                alimentazione.get().getScatti(),
                alimentazione.get().getArrotondaValori(), primoValoreVolumetrico);
        if (scattiMath.getMoltiplicatoreAlimentazione() == 0) {
            System.out.println(
                    "il bagno non richiede aggiunte per ora. nuovo resto scatti : " + scattiAttuali);
            bagno.setScattiTotali(bagno.getScattiTotali() + scattiParziali);
            bagno.setRestoScatti(scattiAttuali);
            bagnoRepository.save(bagno);
            return AlimentazioneScattiRisposta.builder()
                    .idBagno(id)
                    .restoScatti(scattiAttuali)
                    .messaggio(
                            "gli scatti sono inferiori al 90% dell'alimentazione, " +
                                    "le aggiunte non verranno eseguite ma messe in conto per la prossima chiamata.")
                    .moltiplicatoreAlimentazione(0D)
                    .build();
        }
        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione.get())
                        .bagno(bagno)
                        .scattiTotali(bagno.getScattiTotali() + scattiParziali)
                        .restoScatti((int) scattiMath.getRestoScatti())
                        .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                        .build());
        Map<String, String> mappaAggiunta = new HashMap<>();
        for (DettaglioAlimentazione dettaglio : alimentazione.get()
                .getDettaglioAlimentazioneList()) {
            double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto() *
                    scattiMath.getMoltiplicatoreAlimentazione();
            if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
                quantitaProdottoAggiunta = MetodiArrotondamenti.approssimazioneAggiunta(
                        quantitaProdottoAggiunta);
            }
            storicoDettaglioRepository.save(StoricoDettaglio.builder()
                    .prodotto(dettaglio.getProdotto())
                    .storicoGenerale(storicoGenerale)
                    .quantita(quantitaProdottoAggiunta)
                    .unitaDiMisura(dettaglio.getUnitaDiMisura())
                    .build());

            mappaAggiunta.put(dettaglio.getProdotto().getNome(),
                    quantitaProdottoAggiunta + " " + dettaglio.getUnitaDiMisura()
                            .name());
        }

        return AlimentazioneScattiRisposta.builder()
                .idBagno(id)
                .mappaAlimentazione(mappaAggiunta)
                .restoScatti((int) scattiMath.getRestoScatti())
                .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                .build();
    }

}
