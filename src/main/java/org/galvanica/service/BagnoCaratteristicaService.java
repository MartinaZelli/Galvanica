package org.galvanica.service;

import org.galvanica.dto.BagnoCaratteristicaDto;
import org.galvanica.model.Bagno;
import org.galvanica.model.BagnoCaratteristica;
import org.galvanica.repository.BagnoCaratteristicaRepository;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BagnoCaratteristicaService implements ICRUDService<BagnoCaratteristica, BagnoCaratteristicaDto> {
    private final BagnoCaratteristicaRepository repository;
    private final BagnoRepository repositoryBagno;

    public BagnoCaratteristicaService(BagnoCaratteristicaRepository repository, BagnoRepository repositoryBagno) {
        this.repository = repository;
        this.repositoryBagno = repositoryBagno;
    }

    @Override
    public BagnoCaratteristica inserisci(BagnoCaratteristicaDto elemento) {
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
        BagnoCaratteristica bagnoCaratteristica = BagnoCaratteristica.builder()
                .nome(elemento.getNome())
                .bagno(bagnoTrovato.get())
                .descrizione(elemento.getDescrizione())
                .build();
        return repository.save(bagnoCaratteristica);
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
    public BagnoCaratteristica aggiorna(BagnoCaratteristicaDto elemento, long id) {
        Optional<BagnoCaratteristica> caratteristicaOpt = repository.findById(id);
        if (caratteristicaOpt.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        Optional<Bagno> bagnoTrovato = repositoryBagno.findById(elemento.getIdBagno());
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "L'id del bagno non esiste. correggere.");
        }
        BagnoCaratteristica bagnoCaratteristica = caratteristicaOpt.get();
        bagnoCaratteristica.setBagno(bagnoTrovato.get());
        bagnoCaratteristica.setDescrizione(elemento.getDescrizione());
        bagnoCaratteristica.setNome(elemento.getNome());
        return repository.save(bagnoCaratteristica);
    }

    @Override
    public Optional<BagnoCaratteristica> ricercaId(long id) {
        return repository.findById(id);
    }
}
