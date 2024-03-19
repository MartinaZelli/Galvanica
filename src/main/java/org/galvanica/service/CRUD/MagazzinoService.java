package org.galvanica.service.CRUD;

import org.galvanica.dto.MagazzinoDto;
import org.galvanica.model.Magazzino;
import org.galvanica.repository.MagazzinoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MagazzinoService implements ICRUDService<MagazzinoDto> {

    private final MagazzinoRepository magazzinoRepository;

    public MagazzinoService(MagazzinoRepository magazzinoRepository) {
        this.magazzinoRepository = magazzinoRepository;
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
        return MagazzinoDto.builder()
                .idMagazzino(magazzino.getIdMagazzino())
                .descrizione(magazzino.getDescrizione())
                .build();
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
    public MagazzinoDto aggiorna(MagazzinoDto elemento, long id) {
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(id);
        if (magazzinoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        Magazzino magazzino = magazzinoTrovato.get();
        magazzino.setDescrizione(elemento.getDescrizione());
        magazzino = magazzinoRepository.save(magazzino);

        return MagazzinoDto.builder()
                .idMagazzino(magazzino.getIdMagazzino())
                .descrizione(magazzino.getDescrizione())
                .build();
    }

    @Override
    public Optional<MagazzinoDto> ricercaId(long id) {
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(id);
        return magazzinoTrovato
                .map(magazzino -> MagazzinoDto.builder()
                        .descrizione(magazzino.getDescrizione())
                        .idMagazzino(magazzino.getIdMagazzino())
                        .build());
    }
}
