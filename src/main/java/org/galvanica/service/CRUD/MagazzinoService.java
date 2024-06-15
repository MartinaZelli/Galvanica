package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.MagazzinoDto;
import org.galvanica.model.Magazzino;
import org.galvanica.repository.MagazzinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MagazzinoService implements ICRUDService<MagazzinoDto, Magazzino> {

    private final MagazzinoRepository magazzinoRepository;
    private final RelazioneBagnoProdottoService relazioneBagnoProdottoService;

    public MagazzinoService(MagazzinoRepository magazzinoRepository,
                            RelazioneBagnoProdottoService relazioneBagnoProdottoService) {
        this.magazzinoRepository = magazzinoRepository;
        this.relazioneBagnoProdottoService = relazioneBagnoProdottoService;
    }

    @Override
    public MagazzinoDto inserisci(MagazzinoDto elemento) {
        if (elemento.getIdMagazzino() != null) {
            throw new RuntimeException("il'Id magazzino non deve essere valorizzato");
        }
        Magazzino magazzino = Magazzino.builder()
                .descrizione(elemento.getDescrizione())
                .build();
        magazzino = magazzinoRepository.save(magazzino);
        return fromModelToDto(magazzino);
    }

    @Override
    public Boolean elimina(long id) {
        boolean risposta = magazzinoRepository.findById(id).isPresent();
        if (risposta) {
            magazzinoRepository.deleteById(id);
        }
        return risposta;
    }

    @Override
    public MagazzinoDto aggiorna(MagazzinoDto elemento) {
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(elemento.getIdMagazzino());
        if (magazzinoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        Magazzino magazzino = magazzinoTrovato.get();
        magazzino.setDescrizione(elemento.getDescrizione());
        magazzino = magazzinoRepository.save(magazzino);

        return fromModelToDto(magazzino);
    }

    @Override
    public Optional<MagazzinoDto> ricercaId(long id) {
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(id);
        return magazzinoTrovato
                .map(this::fromModelToDto);
    }

    @Override
    public MagazzinoDto fromModelToDto(Magazzino oggettoDaTrasformare) {
        return MagazzinoDto.builder()
                .idMagazzino(oggettoDaTrasformare.getIdMagazzino())
                .descrizione(oggettoDaTrasformare.getDescrizione())
                .build();
    }

    public List<MagazzinoDto> findAllMagazzino() {
        return StreamSupport.stream(magazzinoRepository.findAll().spliterator(),
                false).map(this::fromModelToDto).collect(
                Collectors.toList());
    }
}
