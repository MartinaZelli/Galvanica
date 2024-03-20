package org.galvanica.service;

import org.galvanica.dto.AlimentazioneScattiRisposta;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.ScattiMath;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.model.DettaglioAlimentazione;
import org.galvanica.model.Prodotto;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OperazioniBagnoService {

    private final BagnoRepository bagnoRepository;


    public OperazioniBagnoService(BagnoRepository bagnoRepository) {
        this.bagnoRepository = bagnoRepository;
    }

    public AlimentazioneScattiRisposta faiAlimentazioneScattiApprossimata(Long id,
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
        bagno.setScattiTotali(bagno.getScattiTotali() + scattiParziali);
        int scattiAttuali = bagno.getRestoScatti() + scattiParziali;
        if (((double) scattiAttuali / alimentazione.get().getScatti()) < 0.9) {
            System.out.println(
                    "il bagno non richiede aggiunte per ora. nuovo resto scatti : " + bagno.getRestoScatti());
            bagnoRepository.save(bagno);
            return AlimentazioneScattiRisposta.builder()
                    .idBagno(id)
                    .restoScatti(scattiAttuali)
                    .build();
        }
        //todo alimentazione
        ScattiMath alimentazioneApprossimata = MetodiArrotondamenti.alimentazioneScattiApprossimata(
                scattiAttuali,
                alimentazione.get().getScatti());
        Map<Prodotto, Double> mappaAggiunta = new HashMap<>();
        //todo unità di misura alimentazione non considerate!!!
        for (DettaglioAlimentazione dettaglio : alimentazione.get()
                .getDettaglioAlimentazioneList()) {
            double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto() *
                    alimentazioneApprossimata.getMoltiplicatoreAlimentazione();
            quantitaProdottoAggiunta = MetodiArrotondamenti.approssimazioneAggiunta(
                    quantitaProdottoAggiunta);
            mappaAggiunta.put(dettaglio.getProdotto(), quantitaProdottoAggiunta);
        }

        return AlimentazioneScattiRisposta.builder()
                .idBagno(id)
                .mappaAlimentazione(mappaAggiunta)
                .restoScatti(alimentazioneApprossimata.getRestoScatti())
                .moltiplicatoreAlimentazione(alimentazioneApprossimata.getMoltiplicatoreAlimentazione())
                .build();
    }


}
