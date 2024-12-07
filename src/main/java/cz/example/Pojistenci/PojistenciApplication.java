package cz.example.Pojistenci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Třída, která spouští Spring Boot aplikaci pro správu pojištěnců.
 * Tato třída obsahuje hlavní metodu, která spustí aplikaci.
 */
@EnableJpaRepositories  // Aktivuje podporu pro Spring Data JPA (zajistí, že Spring najde všechny repository)
@SpringBootApplication(scanBasePackages = "cz.example.Pojistenci")  // Označuje hlavní třídu aplikace a specifikuje balíček pro skenování komponent
public class PojistenciApplication {

	/**
	 * Hlavní metoda pro spuštění Spring Boot aplikace.
	 * @param args Parametry příkazového řádku.
	 */
	public static void main(String[] args) {
		// Spuštění Spring Boot aplikace
		SpringApplication.run(PojistenciApplication.class, args);
	}

}
