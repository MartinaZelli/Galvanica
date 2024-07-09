package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.AlimentazioneDto;
import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AlimentazioneService implements ICRUDService<AlimentazioneDto, Alimentazione> {

    private final AlimentazioneRepository alimentazioneRepository;
    private final BagnoRepository bagnoRepository;

    public AlimentazioneService(AlimentazioneRepository alimentazioneRepository,
                                BagnoRepository bagnoRepository) {
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
        if (elemento.getScatti() == null && elemento.getTempo() == null) {
            throw new RuntimeException(
                    "deve essere valorizzato un attributo fra Scatti e Tempo");
        }
        if (elemento.getScatti() != null && elemento.getTempo() != null) {
            throw new RuntimeException(
                    "può essere valorizzato un solo attributo fra Scatti e Tempo");
        }
        if (elemento.getValoriArrotondati() == null) {
            elemento.setValoriArrotondati(false);
        }
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere.");
        }
        if (Objects.equals(elemento.getTipologiaAggiunta(),
                TipologiaAggiunta.SCATTI)) {
            if (bagnoOptional.get().getAlimentazioneList() != null) {
                if (bagnoOptional.get()
                        .getAlimentazioneList()
                        .stream()
                        .anyMatch(alimentazione -> alimentazione.getScatti() != null)) {
                    throw new RuntimeException(
                            "esiste già un alimentazione a scatti per questo bagno.");
                }
            }
        }
        if (elemento.getTipologiaAggiunta().equals(TipologiaAggiunta.MANUALE)) {
            throw new RuntimeException(
                    "non può essere inizializzata un alimentazione Manuale per il bagno.");
        }
        Alimentazione alimentazione = Alimentazione.builder()
                .bagno(bagnoOptional.get())
                .tempo(elemento.getTempo())
                .scatti(elemento.getScatti())
                .descrizione(elemento.getDescrizione())
                .valoriArrotondati(elemento.getValoriArrotondati())
                .build();
        alimentazione = alimentazioneRepository.save(alimentazione);
        return fromModelToDto(alimentazione);
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
    public AlimentazioneDto aggiorna(AlimentazioneDto elemento) {
        Optional<Alimentazione> alimentazioneOptional = alimentazioneRepository.findById(
                elemento.getIdAlimentazione());
        if (alimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "l'id bagno non può essere null");
        }
        if (!Objects.equals(elemento.getIdBagno(),
                alimentazioneOptional.get().getBagno().getIdBagno())) {
            throw new RuntimeException(
                    "non può essere modificato il bagno relativo all'alimentazione.");
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
        if (elemento.getValoriArrotondati() == null) {
            elemento.setValoriArrotondati(false);
        }
        if (!Objects.equals(elemento.getTipologiaAggiunta(),
                alimentazioneOptional.get().getTipologiaAggiunta())) {
            throw new RuntimeException(
                    "non può essere modificata la tipologia di aggiunta di un bagno.");
        }
        if (elemento.getTipologiaAggiunta().equals(TipologiaAggiunta.MANUALE)) {
            throw new RuntimeException(
                    "non può essere inizializzata un alimentazione Manuale per il bagno.");
        }
        Alimentazione alimentazione = alimentazioneOptional.get();
        alimentazione.setDescrizione(elemento.getDescrizione());
        alimentazione.setTempo(elemento.getTempo());
        alimentazione.setScatti(elemento.getScatti());
        alimentazione.setBagno(bagnoOptional.get());
        alimentazione.setValoriArrotondati(elemento.getValoriArrotondati());
        alimentazione = alimentazioneRepository.save(alimentazione);

        return fromModelToDto(alimentazione);
    }

    @Override
    public Optional<AlimentazioneDto> ricercaId(long id) {
        Optional<Alimentazione> alimentazioneOptional =
                alimentazioneRepository.findById(id);
        return alimentazioneOptional
                .map(this::fromModelToDto);
    }

    @Override
    public AlimentazioneDto fromModelToDto(Alimentazione oggettoDaTrasformare) {
        return AlimentazioneDto.builder()
                .idAlimentazione(oggettoDaTrasformare.getIdAlimentazione())
                .idBagno(oggettoDaTrasformare.getBagno().getIdBagno())
                .scatti(oggettoDaTrasformare.getScatti())
                .tempo(oggettoDaTrasformare.getTempo())
                .descrizione(oggettoDaTrasformare.getDescrizione())
                .valoriArrotondati(oggettoDaTrasformare.getValoriArrotondati())
                .nomeBagno(oggettoDaTrasformare.getBagno().getNome())
                .tipologiaAggiunta(oggettoDaTrasformare.getTipologiaAggiunta())
                .build();
    }

    public List<AlimentazioneDto> ricercaAlimentazioneByBagno(Long idBagno) {
        List<Alimentazione> alimentazioneList = alimentazioneRepository.findByIdBagnoOrderScatti(
                idBagno);
        List<AlimentazioneDto> alimentazioneDtoList = new ArrayList<>();
        for (Alimentazione alimentazione : alimentazioneList) {
            alimentazioneDtoList.add(fromModelToDto(alimentazione));
        }
        return alimentazioneDtoList;
    }

    public List<AlimentazioneDto> findAllAlimentazione() {
        return StreamSupport.stream(alimentazioneRepository.findAll().spliterator(),
                false).map(this::fromModelToDto).collect(
                Collectors.toList());
    }

    private void controlloAggiunta(AlimentazioneDto dto, Alimentazione model) {
//todo: controllare se tipologia aggiunta e scatti/tempo sono corrispondenti (tempo: tempo, scatti: scatti)
// controllare che tipologia aggiunta non sia manuale,
// controllare che se tipologia aggiunta di model è x allora deve restare x.
// controllare che sia valorizzato il tempo se Tip.Agg: Tempo e gli scatti...
// controllare che se scatti model valorizzati allora non può diventare tempo e viceversa
// il MODEL fa da verifica a tutti i controlli. l'unica cosa che può variare è o i giorni del tempo o il numero degli scatti..

    }

}
