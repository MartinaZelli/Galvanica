package org.galvanica.service;

import org.galvanica.dto.RelazioneBagnoProdottoDto;

import java.util.Optional;

public class RelazioneBagnoProdottoService implements ICRUDService<RelazioneBagnoProdottoDto> {
    @Override
    public RelazioneBagnoProdottoDto inserisci(RelazioneBagnoProdottoDto elemento) {
        return null;
    }

    @Override
    public Boolean elimina(long id) {
        return null;
    }

    @Override
    public RelazioneBagnoProdottoDto aggiorna(RelazioneBagnoProdottoDto elemento, long id) {
        return null;
    }

    @Override
    public Optional<RelazioneBagnoProdottoDto> ricercaId(long id) {
        return Optional.empty();
    }
}
