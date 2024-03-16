package org.galvanica.service;

import org.galvanica.dto.CaratteristicaBagnoDto;
import org.galvanica.model.Bagno;
import org.galvanica.model.CaratteristicaBagno;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.caratteristicaBagnoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaratteristicaBagnoService implements ICRUDService<CaratteristicaBagnoDto> {
    private final caratteristicaBagnoRepository repository;
    private final BagnoRepository repositoryBagno;

    public CaratteristicaBagnoService(caratteristicaBagnoRepository repository, BagnoRepository repositoryBagno) {
        this.repository = repository;
        this.repositoryBagno = repositoryBagno;
    }

    //TODO? ripulire il codice con:
    // private CaratteristicaBagnoDto CaratteristicaBagnoBuilder() (Oprtional<CaratteristicaBagno>){}
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
        return CaratteristicaBagnoDto.builder()
                .idBagno(caratteristicaBagno.getBagno().getIdBagno())
                .idCaratteristica(caratteristicaBagno.getIdCaratteristica())
                .nome(caratteristicaBagno.getNome())
                .descrizione(caratteristicaBagno.getDescrizione())
                .build();
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
    public CaratteristicaBagnoDto aggiorna(CaratteristicaBagnoDto elemento, long id) {
        Optional<CaratteristicaBagno> caratteristicaOpt = repository.findById(id);
        if (caratteristicaOpt.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        Optional<Bagno> bagnoTrovato = repositoryBagno.findById(elemento.getIdBagno());
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "L'id del bagno non esiste. correggere.");
        }
        CaratteristicaBagno caratteristicaBagno = caratteristicaOpt.get();
        caratteristicaBagno.setBagno(bagnoTrovato.get());
        caratteristicaBagno.setDescrizione(elemento.getDescrizione());
        caratteristicaBagno.setNome(elemento.getNome());
        caratteristicaBagno = repository.save(caratteristicaBagno);
        return CaratteristicaBagnoDto.builder()
                .idBagno(caratteristicaBagno.getBagno().getIdBagno())
                .idCaratteristica(caratteristicaBagno.getIdCaratteristica())
                .nome(caratteristicaBagno.getNome())
                .descrizione(caratteristicaBagno.getDescrizione())
                .build();
    }

    @Override
    public Optional<CaratteristicaBagnoDto> ricercaId(long id) {
        Optional<CaratteristicaBagno> caratteristicaBagnoTrovato =
                repository.findById(id);
        return caratteristicaBagnoTrovato
                .map(caratteristicaBagno -> CaratteristicaBagnoDto.builder()
                        .idBagno(caratteristicaBagno.getBagno().getIdBagno())
                        .idCaratteristica(caratteristicaBagno.getIdCaratteristica())
                        .nome(caratteristicaBagno.getNome())
                        .descrizione(caratteristicaBagno.getDescrizione())
                        .build());
    }
}




























