package org.galvanica.service;

import org.galvanica.dto.DettaglioAlimentazioneDto;

import java.util.Optional;

public class DettaglioAlimentazioneService implements ICRUDService<DettaglioAlimentazioneDto> {
    @Override
    public DettaglioAlimentazioneDto inserisci(DettaglioAlimentazioneDto elemento) {
        return null;
    }

    @Override
    public Boolean elimina(long id) {
        return null;
    }

    @Override
    public DettaglioAlimentazioneDto aggiorna(DettaglioAlimentazioneDto elemento, long id) {
        return null;
    }

    @Override
    public Optional<DettaglioAlimentazioneDto> ricercaId(long id) {
        return Optional.empty();
    }
}
