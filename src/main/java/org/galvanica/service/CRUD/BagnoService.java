package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.model.Bagno;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BagnoService implements ICRUDService<BagnoDto, Bagno> {
    private final BagnoRepository repository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final ProdottoService prodottoService;

    public BagnoService(BagnoRepository repository,
                        StoricoGeneraleRepository storicoGeneraleRepository,
                        ProdottoService prodottoService) {
        this.repository = repository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.prodottoService = prodottoService;
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

        return fromModelToDto(bagno);

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
        return fromModelToDto(bagno);

    }

    public Optional<BagnoDto> ricercaId(long id) {
        Optional<Bagno> bagnoTrovato = repository.findById(id);
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "non esiste bagno con questo ID");
        }
        return bagnoTrovato.map(this::fromModelToDto);
    }

    @Override
    public BagnoDto fromModelToDto(Bagno oggettoDaTrasformare) {
        return BagnoDto.builder()
                .idBagno(oggettoDaTrasformare.getIdBagno())
                .nome(oggettoDaTrasformare.getNome())
                .scattiTotali(oggettoDaTrasformare.getScattiTotali())
                .restoScatti(oggettoDaTrasformare.getRestoScatti())
                .litri(oggettoDaTrasformare.getLitri())
                .build();
    }

    public List<BagnoDto> findAllBagno() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::fromModelToDto)
                .collect(Collectors.toList());
    }

    public Bagno modelRicercaId(long id) {
        Optional<Bagno> bagnoTrovato = repository.findById(id);
        if (bagnoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "non esiste bagno con questo ID");
        }
        return bagnoTrovato.get();
    }

    public List<StoricoGenerale> storicoGeneraleListByBagno(Long idBagno,
                                                            Long limite) {
        return storicoGeneraleRepository.storicoGeneraleListByBagno(idBagno, limite);
    }

    public boolean controllaSeBagnoHaAlimentazioneAScatti(Long idBagno) {
        Optional<Bagno> bagnoOptional = repository.findById(idBagno);
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste bagno con questo ID");
        }
        if (bagnoOptional.get().getAlimentazioneList() != null) {
            return bagnoOptional.get()
                    .getAlimentazioneList()
                    .stream()
                    .anyMatch(alimentazione -> alimentazione.getScatti() != null);
        }
        return false;
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












