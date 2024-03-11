package org.galvanica.service;

import java.util.Optional;

public interface ICRUDService<MODEL, DTO> {
    MODEL inserisci(DTO elemento);

    Boolean elimina(long id);

    MODEL aggiorna(DTO elemento, long id);

    Optional<MODEL> ricercaId(long id);
}
