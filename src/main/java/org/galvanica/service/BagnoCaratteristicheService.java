package org.galvanica.service;

import org.galvanica.dto.BagnoCaratteristicheDto;
import org.galvanica.model.BagnoCaratteristiche;

import java.util.Optional;

public class BagnoCaratteristicheService implements ICRUDService<BagnoCaratteristiche, BagnoCaratteristicheDto> {
    @Override
    public BagnoCaratteristiche inserisci(BagnoCaratteristicheDto elemento) {
        return null;
    }

    @Override
    public Boolean elimina(long id) {
        return null;
    }

    @Override
    public BagnoCaratteristiche aggiorna(BagnoCaratteristicheDto elemento, long id) {
        return null;
    }

    @Override
    public Optional<BagnoCaratteristiche> ricercaId(long id) {
        return Optional.empty();
    }
}
