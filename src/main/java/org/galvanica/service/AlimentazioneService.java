package org.galvanica.service;

import org.galvanica.dto.AlimentazioneDto;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlimentazioneService implements ICRUDService<AlimentazioneDto> {

    private final AlimentazioneRepository alimentazioneRepository;
    private final BagnoRepository bagnoRepository;

    public AlimentazioneService(AlimentazioneRepository alimentazioneRepository, BagnoRepository bagnoRepository) {
        this.alimentazioneRepository = alimentazioneRepository;
        this.bagnoRepository = bagnoRepository;
    }

    @Override
    public AlimentazioneDto inserisci(AlimentazioneDto elemento) {
        if (elemento.getIdAlimentazione() != null) {
            throw new RuntimeException(
                    "l'id deve essere autoincrementale, non inizializzare");
        }
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "l'id bagno deve essere valorizzato");
        }
        if (elemento.getScatti() != null && elemento.getTempo() != null) {
            throw new RuntimeException(
                    "può essere valorizzato un solo attributo fra Scatti e Tempo");
        }
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere.");
        }
        Alimentazione alimentazione = Alimentazione.builder()
                .bagno(bagnoOptional.get())
                .tempo(elemento.getTempo())
                .scatti(elemento.getScatti())
                .descrizione(elemento.getDescrizione())
                .build();
        alimentazione = alimentazioneRepository.save(alimentazione);
        return AlimentazioneDto.builder()
                .idAlimentazione(alimentazione.getIdAlimentazione())
                .IdBagno(alimentazione.getBagno().getIdBagno())
                .scatti(alimentazione.getScatti())
                .tempo(alimentazione.getTempo())
                .descrizione(alimentazione.getDescrizione())
                .build();
    }

    @Override
    public Boolean elimina(long id) {
        boolean risposta = alimentazioneRepository.findById(id).isPresent();
        if (risposta) {
            alimentazioneRepository.deleteById(id);
        }
        return risposta;
    }

    @Override
    public AlimentazioneDto aggiorna(AlimentazioneDto elemento, long id) {
        Optional<Alimentazione> alimentazioneOptional = alimentazioneRepository.findById(
                id);
        if (alimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "l'id bagno non può essere null");
        }
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere");
        }
        if (elemento.getScatti() != null && elemento.getTempo() != null) {
            throw new RuntimeException(
                    "può essere valorizzato un solo attributo fra Scatti e Tempo");
        }
        Alimentazione alimentazione = alimentazioneOptional.get();
        alimentazione.setDescrizione(elemento.getDescrizione());
        alimentazione.setTempo(elemento.getTempo());
        alimentazione.setScatti(elemento.getScatti());
        alimentazione.setBagno(bagnoOptional.get());
        alimentazione = alimentazioneRepository.save(alimentazione);

        return AlimentazioneDto.builder()
                .idAlimentazione(alimentazione.getIdAlimentazione())
                .descrizione(alimentazione.getDescrizione())
                .tempo(alimentazione.getTempo())
                .IdBagno(alimentazione.getBagno().getIdBagno())
                .scatti(alimentazione.getScatti())
                .build();
    }

    @Override
    public Optional<AlimentazioneDto> ricercaId(long id) {
        Optional<Alimentazione> alimentazioneOptional =
                alimentazioneRepository.findById(id);
        return alimentazioneOptional
                .map(alimentazione -> AlimentazioneDto.builder()
                        .idAlimentazione(alimentazione.getIdAlimentazione())
                        .descrizione(alimentazione.getDescrizione())
                        .tempo(alimentazione.getTempo())
                        .IdBagno(alimentazione.getBagno().getIdBagno())
                        .scatti(alimentazione.getScatti())
                        .build());
    }
}
