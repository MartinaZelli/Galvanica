package org.galvanica.service;

import org.galvanica.dto.AlimentazioneDto;

import java.util.Optional;

public class AlimentazioneService implements ICRUDService<AlimentazioneDto> {
    @Override
    public AlimentazioneDto inserisci(AlimentazioneDto elemento) {
        return null;
    }

    @Override
    public Boolean elimina(long id) {
        return null;
    }

    @Override
    public AlimentazioneDto aggiorna(AlimentazioneDto elemento, long id) {
        return null;
    }

    @Override
    public Optional<AlimentazioneDto> ricercaId(long id) {
        return Optional.empty();
    }
}
