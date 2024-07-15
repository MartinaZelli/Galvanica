package org.galvanica.repository;

import org.galvanica.model.Bagno;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BagnoRepository extends CrudRepository<Bagno, Long> {

	@Query(value = "SELECT *\n" +
		"FROM bagno\n" +
		"    INNER JOIN alimentazione\n" +
		"ON bagno.id_bagno = alimentazione.bagno_id_bagno\n" +
		"WHERE alimentazione.scatti IS NOT NULL\n", nativeQuery = true)
	List<Bagno> findAllBagnoIfAlimentazioneScattiNotNull();

}
