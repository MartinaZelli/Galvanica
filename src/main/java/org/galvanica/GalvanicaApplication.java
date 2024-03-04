package org.galvanica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication()
public class GalvanicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GalvanicaApplication.class, args);
	}

}
