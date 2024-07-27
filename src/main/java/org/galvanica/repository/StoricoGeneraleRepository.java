package org.galvanica.repository;

import org.galvanica.math.TipologiaAggiunta;
import org.galvanica.model.StoricoGenerale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoricoGeneraleRepository extends CrudRepository<StoricoGenerale, Long> {

    @Query(value = "SELECT * FROM storico_generale " +
            "WHERE concluso = ?1 AND bagno_id_bagno = ?2 AND tipologia_aggiunta like ?3 " +
            "ORDER BY data_creazione DESC", nativeQuery = true)
    List<StoricoGenerale> storicoGeneraleDescList(boolean concluso, Long idBagno,
                                                  TipologiaAggiunta tipologiaAggiunta);

    @Query(value = "SELECT * FROM storico_generale " +
            "WHERE bagno_id_bagno = ?1 AND tipologia_aggiunta like 'TEMPO' " +
            "ORDER BY data_creazione DESC " +
            "LIMIT 1", nativeQuery = true)
    StoricoGenerale storicoGeneraleTempoLast(Long idBagno);

    @Query(value = """
            SELECT
                *
            FROM storico_generale
            WHERE
                bagno_id_bagno = ?1
            ORDER BY
                data_creazione DESC, concluso
            LIMIT ?2""", nativeQuery = true)
    List<StoricoGenerale> storicoGeneraleListByBagno(Long idBagno, Long limite);

    @Query(value = """
            SELECT
                *
            FROM storico_generale
            WHERE
                bagno_id_bagno = ?1
                AND tipologia_aggiunta like 'SCATTI'
            ORDER BY
                data_creazione DESC
            LIMIT 1""", nativeQuery = true)
    StoricoGenerale ultimoStoricoGeneraleScatti(Long idBagno);


    @Query(value = """
            select
                *
            from storico_generale
            where
                bagno_id_bagno = :idBagno
                and tipologia_aggiunta = 'TEMPO'
                and not annullato_generale
                and id_storico not in (:idStoricoList)
            ORDER BY
                data_creazione DESC
            LIMIT 1
            """, nativeQuery = true)
    StoricoGenerale storicoATempoNonInLista(Long idBagno, List<Long> idStoricoList);

}
