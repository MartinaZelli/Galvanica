package org.galvanica.service;

import org.galvanica.dto.BagnoDto;
import org.galvanica.model.Bagno;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BagnoService implements ICRUDService<Bagno, BagnoDto> {
    private final BagnoRepository repository;

    public BagnoService(BagnoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Bagno inserisci(BagnoDto elemento) {
        if (elemento.getNome() == null) {
            throw new RuntimeException(
                    "Il nome deve essere valorizzato stronzo porcoddio");
        }
        if (elemento.getLitri() == null || elemento.getLitri() == 0) {
            throw new RuntimeException("I litri devono essere inizializzati");
        }
        if (elemento.getIdBagno() != null) {
            throw new RuntimeException(
                    "l'id deve essere autoincrementale, non inizializzare");
        }
        if (elemento.getScattiTotali() == null)
            elemento.setScattiTotali(0);
        if (elemento.getRestoScatti() == null)
            elemento.setRestoScatti(0);
        Bagno bagno = Bagno.builder()
                .nome(elemento.getNome())
                .litri(elemento.getLitri())
                .scattiTotali(elemento.getScattiTotali())
                .restoScatti(elemento.getRestoScatti())
                .build();
        return repository.save(bagno);
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
    public Bagno aggiorna(BagnoDto elemento, long id) {
        Optional<Bagno> bagno = repository.findById(id);
        if (bagno.isEmpty()) {
            throw new RuntimeException(
                    "metti un id corretto che questo non c'è, bischero.");
        }
        Bagno b = bagno.get();
        b.setNome(elemento.getNome());
        b.setRestoScatti(elemento.getRestoScatti());
        b.setScattiTotali(elemento.getScattiTotali());
        b.setLitri(elemento.getLitri());

        return repository.save(b);
    }

    @Override
    public Optional<Bagno> ricercaId(long id) {
        return repository.findById(id);
    }
}
