package cz.example.Pojistenci.repositories;

import cz.example.Pojistenci.models.Pojistenec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository pro entitu Pojistenec (pojištěnec), která zajišťuje komunikaci s databází
@Repository
public interface PojistenecRepository extends JpaRepository<Pojistenec, Long> {

    // Metoda pro vyhledání pojištěnců, jejichž jméno a příjmení začínají zadanými řetězci.
    // Používá stránkování (Pageable) pro omezení výsledků na jednu stránku.
    // Vráti stránku (Page) s pojištěnci odpovídajícími hledaným kritériím.
    Page<Pojistenec> findByJmenoStartingWithAndPrijmenniStartingWith(String jmeno, String prijmeni, Pageable pageable);

    // Metoda pro vyhledání pojištěnců, jejichž jméno a příjmení začínají zadanými řetězci.
    // Neprovádí stránkování, vrací pouze seznam pojištěnců (List).
    List<Pojistenec> findByJmenoStartingWithAndPrijmenniStartingWith(String jmeno, String prijmeni);
}
