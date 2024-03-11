package org.galvanica.service;

import org.galvanica.dto.BagnoCaratteristicaDto;
import org.galvanica.model.BagnoCaratteristica;
import org.galvanica.repository.BagnoCaratteristicaRepository;

import java.util.Optional;

public class BagnoCaratteristicaService implements ICRUDService<BagnoCaratteristica, BagnoCaratteristicaDto> {
    private final BagnoCaratteristicaRepository repository;

    public BagnoCaratteristicaService(BagnoCaratteristicaRepository repository) {
        this.repository = repository;
    }

    @Override
    public BagnoCaratteristica inserisci(BagnoCaratteristicaDto elemento) {
        if (elemento.getBagno() == null) {
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
        if (elemento.getDescrizione() == null) {
            elemento.setDescrizione(null);
        }
        /*todo: cercare se il l'id bagno è presente altrimenti ritornare errore.
           Se è sbagliato legare troppe classi come si fa?? è da verificare dopo o qui?*/
        BagnoCaratteristica bagnoCaratteristica = BagnoCaratteristica.builder()
                .nome(elemento.getNome())
                .bagno(elemento.getBagno())
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
        BagnoCaratteristica bagnoCaratteristica = caratteristicaOpt.get();
        bagnoCaratteristica.setBagno(elemento.getBagno());
        bagnoCaratteristica.setDescrizione(elemento.getDescrizione());
        bagnoCaratteristica.setNome(elemento.getNome());
        return repository.save(bagnoCaratteristica);
    }

    @Override
    public Optional<BagnoCaratteristica> ricercaId(long id) {
        return repository.findById(id);
    }
}
