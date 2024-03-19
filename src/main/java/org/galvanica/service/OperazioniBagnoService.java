package org.galvanica.service;

import org.galvanica.dto.AlimentazioneScattiRisposta;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.DettaglioAlimentazioneRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OperazioniBagnoService {

    private final BagnoRepository bagnoRepository;
    private final DettaglioAlimentazioneRepository dettaglioAlimentazioneRepository;
    private final AlimentazioneRepository alimentazioneRepository;


    public OperazioniBagnoService(BagnoRepository bagnoRepository,
                                  DettaglioAlimentazioneRepository dettaglioAlimentazioneRepository,
                                  AlimentazioneRepository alimentazioneRepository) {
        this.bagnoRepository = bagnoRepository;
        this.dettaglioAlimentazioneRepository = dettaglioAlimentazioneRepository;
        this.alimentazioneRepository = alimentazioneRepository;
    }

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
        bagno.setScattiTotali(bagno.getScattiTotali() + scattiParziali);
        Integer scattiAttuali = bagno.getRestoScatti() + scattiParziali;
        if (((double) scattiAttuali / alimentazione.get().getScatti()) < 0.9) {
            bagno.setRestoScatti(scattiAttuali);
            System.out.println(
                    "il bagno non richiede aggiunte per ora. nuovo resto scatti : " + bagno.getRestoScatti());
            bagnoRepository.save(bagno);
            return null;
        }
        //todo alimentazione
        return null;
    }


}
