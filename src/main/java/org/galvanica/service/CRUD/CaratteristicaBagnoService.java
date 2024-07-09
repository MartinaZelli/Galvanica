package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.CaratteristicaBagnoDto;
import org.galvanica.model.Bagno;
import org.galvanica.model.CaratteristicaBagno;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.CaratteristicaBagnoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CaratteristicaBagnoService implements ICRUDService<CaratteristicaBagnoDto, CaratteristicaBagno> {
    private final CaratteristicaBagnoRepository repository;
    private final BagnoRepository repositoryBagno;
    private final CaratteristicaBagnoRepository caratteristicaBagnoRepository;

    public CaratteristicaBagnoService(CaratteristicaBagnoRepository repository,
                                      BagnoRepository repositoryBagno,
                                      CaratteristicaBagnoRepository caratteristicaBagnoRepository) {
        this.repository = repository;
        this.repositoryBagno = repositoryBagno;
        this.caratteristicaBagnoRepository = caratteristicaBagnoRepository;
    }

    @Override
    public CaratteristicaBagnoDto inserisci(CaratteristicaBagnoDto elemento) {
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "Il Bagno deve essere valorizzato");
        }
        if (elemento.getNome() == null) {
            throw new RuntimeException(
                    "Il nome deve essere valorizzato");
        }
        if (elemento.getIdCaratteristica() != null) {
            throw new RuntimeException(
                    "L'id è autoincrementale, non inizializzare");
        }
        Optional<Bagno> bagnoTrovato = repositoryBagno.findById(elemento.getIdBagno());
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "L'id del bagno non esiste. correggere.");
        }
        CaratteristicaBagno caratteristicaBagno = CaratteristicaBagno.builder()
                .nome(elemento.getNome())
                .bagno(bagnoTrovato.get())
                .descrizione(elemento.getDescrizione())
                .build();
        caratteristicaBagno = repository.save(caratteristicaBagno);
        return fromModelToDto(caratteristicaBagno);
    }

    @Override
    public Boolean elimina(long id) {
        boolean risposta = repository.findById(id).isPresent();
        if (risposta) {
            repository.deleteById(id);
        }
        return risposta;
    }

    @Override
    public CaratteristicaBagnoDto aggiorna(CaratteristicaBagnoDto elemento) {

        Optional<CaratteristicaBagno> caratteristicaOpt = repository.findById(
                elemento.getIdCaratteristica());
        if (caratteristicaOpt.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "il bagno non può essere null");
        }
        Optional<Bagno> bagnoTrovato = repositoryBagno.findById(elemento.getIdBagno());
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "L'id del bagno non esiste. correggere.");
        }
        if (elemento.getNome() == null) {
            throw new RuntimeException(
                    "Il nome deve essere valorizzato");
        }
        CaratteristicaBagno caratteristicaBagno = caratteristicaOpt.get();
        caratteristicaBagno.setBagno(bagnoTrovato.get());
        caratteristicaBagno.setDescrizione(elemento.getDescrizione());
        caratteristicaBagno.setNome(elemento.getNome());
        caratteristicaBagno = repository.save(caratteristicaBagno);
        return fromModelToDto(caratteristicaBagno);
    }

    @Override
    public Optional<CaratteristicaBagnoDto> ricercaId(long id) {
        Optional<CaratteristicaBagno> caratteristicaBagnoTrovato =
                repository.findById(id);
        return caratteristicaBagnoTrovato
                .map(this::fromModelToDto);
    }


    @Override
    public CaratteristicaBagnoDto fromModelToDto(
            CaratteristicaBagno oggettoDaTrasformare) {

        return CaratteristicaBagnoDto.builder()
                .idCaratteristica(oggettoDaTrasformare.getIdCaratteristica())
                .idBagno(oggettoDaTrasformare.getBagno().getIdBagno())
                .nome(oggettoDaTrasformare.getNome())
                .descrizione(oggettoDaTrasformare.getDescrizione())
                .build();
    }

    public List<CaratteristicaBagnoDto> findAllCaratteristicaBagno() {
        return StreamSupport.stream(caratteristicaBagnoRepository.findAll()
                        .spliterator(),
                false).map(this::fromModelToDto).collect(
                Collectors.toList());
    }
}




























