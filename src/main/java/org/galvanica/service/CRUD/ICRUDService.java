package org.galvanica.service.CRUD;

import java.util.Optional;

public interface ICRUDService<DTO> {
    DTO inserisci(DTO elemento);

    Boolean elimina(long id);

    DTO aggiorna(DTO elemento, long id);

    Optional<DTO> ricercaId(long id);

}
