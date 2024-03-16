package org.galvanica.service;

import org.galvanica.dto.BagnoDto;
import org.galvanica.model.Bagno;
import org.galvanica.repository.BagnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class BagnoService implements ICRUDService<BagnoDto> {
    private final BagnoRepository repository;

    public BagnoService(BagnoRepository repository) {
        this.repository = repository;
    }

    @Override
    public BagnoDto inserisci(BagnoDto elemento) {
        if (elemento.getNome() == null) {
            throw new RuntimeException(
                    "Il nome deve essere valorizzato");
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
                .dataInizio(LocalDate.now())
                .dataFine(null)
                .build();
        bagno = repository.save(bagno);
        return BagnoDto.builder()
                .nome(bagno.getNome())
                .idBagno(bagno.getIdBagno())
                .litri(bagno.getLitri())
                .restoScatti(bagno.getRestoScatti())
                .scattiTotali(bagno.getScattiTotali())
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
    public BagnoDto aggiorna(BagnoDto elemento, long id) {
        Optional<Bagno> bagnoTrovato = repository.findById(id);
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "metti un id corretto che questo non c'è, bischero.");
        }
        Bagno bagno = bagnoTrovato.get();
        bagno.setNome(elemento.getNome());
        bagno.setRestoScatti(elemento.getRestoScatti());
        bagno.setScattiTotali(elemento.getScattiTotali());
        bagno.setLitri(elemento.getLitri());
        bagno = repository.save(bagno);
        return BagnoDto.builder()
                .nome(bagno.getNome())
                .idBagno(bagno.getIdBagno())
                .litri(bagno.getLitri())
                .restoScatti(bagno.getRestoScatti())
                .scattiTotali(bagno.getScattiTotali())
                .build();
    }

    public Optional<BagnoDto> ricercaId(long id) {
        Optional<Bagno> bagnoTrovato = repository.findById(id);
        return bagnoTrovato.map(bagno -> BagnoDto.builder()
                .nome(bagno.getNome())
                .idBagno(bagno.getIdBagno())
                .litri(bagno.getLitri())
                .restoScatti(bagno.getRestoScatti())
                .scattiTotali(bagno.getScattiTotali())
                .build());
    }
    //TODO: mettere Optional BagnoDto oppure senza Optional? decidere quale è il migliore.
   /* @Override
    public BagnoDto ricercaId(long id) {
        Optional<Bagno> bagnoTrovato = repository.findById(id);
        return bagnoTrovato.map(bagno -> BagnoDto.builder()
                .nome(bagno.getNome())
                .idBagno(bagno.getIdBagno())
                .litri(bagno.getLitri())
                .restoScatti(bagno.getRestoScatti())
                .scattiTotali(bagno.getScattiTotali())
                .build()).orElse(null);
    }*/

}












