package org.galvanica.repository;

import org.galvanica.dto.StoricoTotaleDto;
import org.galvanica.model.StoricoDettaglio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface StoricoDettaglioRepository extends CrudRepository<StoricoDettaglio, Long> {

    @Query(value = """
            SELECT
                *
            FROM storico_dettaglio
            WHERE storico_generale_id_storico = ?1
              AND annullato_dettaglio = ?2
              AND eseguito_dettaglio = ?3""",
            nativeQuery = true)
    List<StoricoDettaglio> storicoDettaglioList(Long idStoricoGenerale,
                                                boolean annullato,
                                                boolean eseguito);

    @Query(value = """
            SELECT
                *
                  FROM storico_generale
                  INNER JOIN storico_dettaglio
                  ON storico_generale.id_storico = storico_dettaglio.storico_generale_id_storico
                  WHERE bagno_id_bagno = ?1
                  ORDER BY storico_generale.eseguito_generale,
                           storico_generale.data_creazione DESC
                  LIMIT ?2""",
            nativeQuery = true)
    List<StoricoDettaglio> findByIdBagno(Long idBagno, long limit);

    @Query(value = """
            SELECT
                sd.id_storico_dettaglio
            FROM storico_dettaglio sd
            JOIN storico_generale sg
            ON sd.storico_generale_id_storico = sg.id_storico
            WHERE sd.id_storico_dettaglio IN (?1)
            ORDER BY sg.bagno_id_bagno, sg.data_creazione""", nativeQuery = true)
    List<Long> orderAscIdList(List<Long> idDettaglio);

    @Query(value = """
            select sg.id_storico as idStoricoGenerale,
                   sd.id_storico_dettaglio as idStoricoDettaglio,
                   sg.bagno_id_bagno as idBagno,
                   b.nome as nomeBagno,
                   sd.prodotto_id_prodotto as idProdotto,
                   p.nome as nomeProdotto,
                   sd.quantita as quantita,
                   sd.unita_di_misura as unitaDiMisura,
                   sd.eseguito_dettaglio as eseguito,
                   sd.annullato_dettaglio as escluso,
                   sg.data_creazione as dataCreazione,
                   sg.data_esecuzione as dataFine,
                   sg.tipologia_aggiunta as tipologiaAggiunta
            FROM galvanica.storico_dettaglio sd
                left JOIN storico_generale sg on sg.id_storico = sd.storico_generale_id_storico
                left join bagno b on sg.bagno_id_bagno = b.id_bagno
                left join prodotto p on sd.prodotto_id_prodotto = p.id_prodotto
            limit ?1""",
            nativeQuery = true)
    List<StoricoTotaleDto> listaStoricoSemplificato(int limite);

    @Query(value = """
            SELECT
                *
            FROM
                storico_dettaglio sd
            JOIN galvanica.storico_generale sg on sg.id_storico = sd.storico_generale_id_storico
            WHERE
                sg.bagno_id_bagno = :idBagno
              AND (NOT sd.annullato_dettaglio OR sd.annullato_dettaglio is null)
              AND (NOT sd.eseguito_dettaglio OR sd.eseguito_dettaglio is null)
            ORDER BY sg.data_creazione DESC""", nativeQuery = true)
    List<StoricoDettaglio> listaStoriciDettagliDaGestireByBagno(Long idBagno);

    @Query(value = """
            SELECT
                *
            FROM
                storico_dettaglio sd
            JOIN galvanica.storico_generale sg on sg.id_storico = sd.storico_generale_id_storico
            WHERE
                sg.data_creazione BETWEEN :dataInizio AND :dataFine
              AND (NOT sd.annullato_dettaglio OR sd.annullato_dettaglio is null)
              AND (NOT sd.eseguito_dettaglio OR sd.eseguito_dettaglio is null)
            ORDER BY sg.data_creazione DESC""", nativeQuery = true)
    List<StoricoDettaglio> listaStoriciDettagliDaGestireByDate(LocalDate dataInizio,
                                                               LocalDate dataFine);

    @Query(value = """
            SELECT
                *
            FROM
                storico_dettaglio sd
            JOIN galvanica.storico_generale sg on sg.id_storico = sd.storico_generale_id_storico
            WHERE
                sg.bagno_id_bagno = :idBagno
              AND sg.data_creazione BETWEEN :dataInizio AND :dataFine
              AND (NOT sd.annullato_dettaglio OR sd.annullato_dettaglio is null)
              AND (NOT sd.eseguito_dettaglio OR sd.eseguito_dettaglio is null)
            ORDER BY sg.data_creazione DESC""", nativeQuery = true)
    List<StoricoDettaglio> listaStoriciDettagliDaGestireByDateEBagno(
            LocalDate dataInizio, LocalDate dataFine, Long idBagno);

    List<StoricoDettaglio> findByStoricoGeneraleIdStorico(Long idStoricoGenerale);
}
