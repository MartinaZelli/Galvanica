package org.galvanica.repository;

import org.galvanica.model.Alimentazione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlimentazioneRepository extends CrudRepository<Alimentazione, Long> {

    @Query(value = "SELECT * FROM alimentazione WHERE bagno_id_bagno = ?1 AND tempo LIKE concat('%', ?2, '%')",
            nativeQuery = true)
    List<Alimentazione> findByTempo(Long id, String dayOfWeek);

    @Query(value = "SELECT * FROM alimentazione WHERE bagno_id_bagno = :id ORDER BY scatti", nativeQuery = true)
    List<Alimentazione> findByIdBagnoOrderScatti(Long id);

}
