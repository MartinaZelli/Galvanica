package org.galvanica.repository;

import org.galvanica.model.StoricoGenerale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoricoGeneraleRepository extends CrudRepository<StoricoGenerale, Long> {
    @Query(value = "SELECT * FROM storico_generale " +
            "WHERE concluso = ?1 AND bagno_id_bagno = ?2 AND sono_scatti = ?3 " +
            "ORDER BY data_creazione DESC", nativeQuery = true)
    List<StoricoGenerale> storicoGeneraleDescList(boolean concluso,
                                                  Long idBagno, boolean sonoScatti);

    @Query(value = "SELECT * FROM storico_generale " +
            "WHERE bagno_id_bagno = ?1 AND sono_scatti = false " +
            "ORDER BY data_creazione DESC " +
            "LIMIT 1", nativeQuery = true)
    StoricoGenerale storicoGeneraleTempoLast(Long idBagno);

}
