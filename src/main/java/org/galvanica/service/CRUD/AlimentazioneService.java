package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.AlimentazioneDto;
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
        if (elemento.getArrotondaValori() == null) {
            elemento.setArrotondaValori(false);
        }
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere.");
        }
        if (Objects.equals(elemento.getTipoAlimentazione(), "Scatti")) {
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
        Alimentazione alimentazione = Alimentazione.builder()
                .bagno(bagnoOptional.get())
                .tempo(elemento.getTempo())
                .scatti(elemento.getScatti())
                .descrizione(elemento.getDescrizione())
                .arrotondaValori(elemento.getArrotondaValori())
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
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere");
        }
        if (elemento.getScatti() != null && elemento.getTempo() != null) {
            throw new RuntimeException(
                    "può essere valorizzato un solo attributo fra Scatti e Tempo");
        }
        if (elemento.getArrotondaValori() == null) {
            elemento.setArrotondaValori(false);
        }
        Alimentazione alimentazione = alimentazioneOptional.get();
        alimentazione.setDescrizione(elemento.getDescrizione());
        alimentazione.setTempo(elemento.getTempo());
        alimentazione.setScatti(elemento.getScatti());
        alimentazione.setBagno(bagnoOptional.get());
        alimentazione.setArrotondaValori(elemento.getArrotondaValori());
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
        String tipoAlimentazione = "";
        if (oggettoDaTrasformare.getScatti() != null) {
            tipoAlimentazione = "Scatti";
        }
        if (oggettoDaTrasformare.getTempo() != null) {
            tipoAlimentazione = "Tempo";
        }
        return AlimentazioneDto.builder()
                .idAlimentazione(oggettoDaTrasformare.getIdAlimentazione())
                .idBagno(oggettoDaTrasformare.getBagno().getIdBagno())
                .scatti(oggettoDaTrasformare.getScatti())
                .tempo(oggettoDaTrasformare.getTempo())
                .descrizione(oggettoDaTrasformare.getDescrizione())
                .arrotondaValori(oggettoDaTrasformare.getArrotondaValori())
                .nomeBagno(oggettoDaTrasformare.getBagno().getNome())
                .tipoAlimentazione(tipoAlimentazione)
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

}
