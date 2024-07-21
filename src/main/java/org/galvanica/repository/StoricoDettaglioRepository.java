package org.galvanica.repository;

import org.galvanica.dto.StoricoTotaleDto;
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

    @Query(value = "select sg.id_storico as idStoricoGenerale,\n" +
            "       sd.id_storico_dettaglio as idStoricoDettaglio,\n" +
            "       sg.bagno_id_bagno as idBagno,\n" +
            "       b.nome as nomeBagno,\n" +
            "       sd.prodotto_id_prodotto as idProdotto,\n" +
            "       p.nome as nomeProdotto,\n" +
            "       sd.quantita as quantita,\n" +
            "       sd.unita_di_misura as unitaDiMisura,\n" +
            "       sd.eseguito as eseguito,\n" +
            "       sd.escluso as escluso,\n" +
            "       sg.data_creazione as dataCreazione,\n" +
            "       sg.data_fine as dataFine,\n" +
            "       sg.tipologia_aggiunta as tipologiaAggiunta\n" +
            "FROM galvanica.storico_dettaglio sd\n" +
            "left JOIN storico_generale sg on sg.id_storico = sd.storico_generale_id_storico\n" +
            "left join bagno b on sg.bagno_id_bagno = b.id_bagno\n" +
            "left join prodotto p on sd.prodotto_id_prodotto = p.id_prodotto\n" +
            "limit ?1", nativeQuery = true)
    List<StoricoTotaleDto> listaStoricoSemplificato(int limite);


    List<StoricoDettaglio> findByStoricoGeneraleIdStorico(Long idStoricoGenerale);
}
