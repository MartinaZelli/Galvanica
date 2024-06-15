package org.galvanica.service.CRUD;

import java.util.Optional;

public interface ICRUDService<DTO, MODEL> {
    DTO inserisci(DTO elemento);

    Boolean elimina(long id);

    DTO aggiorna(DTO elemento);

    Optional<DTO> ricercaId(long id);

    DTO fromModelToDto(MODEL oggettoDaTrasformare);

}
