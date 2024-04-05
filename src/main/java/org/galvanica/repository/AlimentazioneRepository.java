package org.galvanica.repository;

import org.galvanica.model.Alimentazione;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlimentazioneRepository extends CrudRepository<Alimentazione, Long> {

    @Query(value = "SELECT * FROM alimentazione " +
            "WHERE tempo LIKE concat('%', ?1, '%')", nativeQuery = true)
    List<Alimentazione> findByTempo(String dayOfWeek);


}
