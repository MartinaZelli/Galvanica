package org.galvanica.repository;

import org.galvanica.model.StoricoGenerale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoricoGeneraleRepository extends CrudRepository<StoricoGenerale, Long> {
    @Query(value = "SELECT * FROM storico_generale " +
            "WHERE concluso = ?1 AND bagno_id_bagno = ?2 " +
            "ORDER BY data_creazione DESC", nativeQuery = true)
    List<StoricoGenerale> storicoGeneraleDescList(boolean concluso, Long idBagno);

}
