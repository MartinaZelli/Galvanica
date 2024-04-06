package org.galvanica.repository;

import org.galvanica.model.StoricoDettaglio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoricoDettaglioRepository extends CrudRepository<StoricoDettaglio, Long> {

    @Query(value = "SELECT * FROM storico_dettaglio " +
            "WHERE storico_generale_id_storico = ?1 AND escluso = ?2 AND eseguito = ?3 ", nativeQuery = true)
    List<StoricoDettaglio> storicoDettaglioList(Long idStoricoGenerale,
                                                boolean escluso,
                                                boolean eseguito);
}
