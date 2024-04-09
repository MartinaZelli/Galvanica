package org.galvanica.repository;

import org.galvanica.model.DettaglioAlimentazione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DettaglioAlimentazioneRepository extends CrudRepository<DettaglioAlimentazione, Long> {

    @Query(value = "SELECT *\n" +
            "FROM alimentazione\n" +
            "INNER JOIN dettaglio_alimentazione\n" +
            "ON alimentazione.id_alimentazione = dettaglio_alimentazione.alimentazione_id_alimentazione\n" +
            "WHERE bagno_id_bagno = ?1\n" +
            "ORDER BY alimentazione.id_alimentazione\n", nativeQuery = true)
    List<DettaglioAlimentazione> findByIdBagnoOrderScatti(Long id);
}
