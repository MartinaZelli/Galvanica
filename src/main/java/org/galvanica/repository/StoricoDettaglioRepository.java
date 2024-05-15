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
            "ORDER BY storico_generale.concluso,storico_generale.data_creazione DESC\n" +
            "LIMIT ?2", nativeQuery = true)
    List<StoricoDettaglio> findByIdBagno(Long idBagno, long limit);

    @Query(value = "SELECT sd.id_storico_dettaglio\n" +
            "FROM storico_dettaglio sd\n" +
            "JOIN storico_generale sg\n" +
            "    ON sd.storico_generale_id_storico = sg.id_storico\n" +
            "WHERE sd.id_storico_dettaglio IN (?1)\n" +
            "ORDER BY sg.bagno_id_bagno, sg.data_creazione ASC ", nativeQuery = true)
    List<Long> orderAscIdList(List<Long> idDettaglio);

}
