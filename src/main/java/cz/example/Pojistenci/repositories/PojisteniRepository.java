package cz.example.Pojistenci.repositories;

import cz.example.Pojistenci.models.Pojistenec;
import cz.example.Pojistenci.models.Pojisteni;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository pro entitu Pojisteni (pojištění), která zajišťuje komunikaci s databází
@Repository
public interface PojisteniRepository extends JpaRepository<Pojisteni, Long> {

    // Metoda pro vyhledání všech pojištění pro konkrétního pojištěnce na základě jeho ID.
    // Vrací seznam (List) všech pojištění, která jsou přiřazena danému pojištěnci.
    List<Pojisteni> findByPojistenecId(Long pojistenecId);

    // Metoda pro vyhledání pojištění na základě jména a příjmení pojištěnce.
    // Používá LIKE operátor pro vyhledávání, které obsahuje zadaný text.
    // Výsledek je stránkovaný (Page), což znamená, že bude vrácena pouze část záznamů na jedné stránce.
    @Query("SELECT p FROM Pojisteni p WHERE CONCAT(p.pojistenec.jmeno, ' ', p.pojistenec.prijmenni) LIKE %:query%")
    Page<Pojisteni> findByPojistenecNameContaining(@Param("query") String query, Pageable pageable);
}
