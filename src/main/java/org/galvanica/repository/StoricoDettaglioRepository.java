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

    @Query(value = "SELECT *\n" +
            "FROM storico_generale\n" +
            "INNER JOIN storico_dettaglio\n" +
            "ON storico_generale.id_storico = storico_dettaglio.storico_generale_id_storico\n" +
            "WHERE bagno_id_bagno = ?1\n" +
            "ORDER BY storico_generale.concluso,storico_generale.data_creazione DESC", nativeQuery = true)
    List<StoricoDettaglio> findByIdBagno(Long idBagno);
}
