package cz.example.Pojistenci.service;

import cz.example.Pojistenci.models.Pojistenec;
import cz.example.Pojistenci.models.Pojisteni;
import cz.example.Pojistenci.repositories.PojistenecRepository;
import cz.example.Pojistenci.repositories.PojisteniRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PojisteniService {

    private final PojisteniRepository pojisteniRepository;  // Repository pro práci s pojištěními
    private PojistenecRepository repository;  // Repository pro práci s pojištěnci
    public PojistenecService service;  // Service pro manipulaci s pojištěnci

    // Konstruktor pro injektování závislostí
    @Autowired
    public PojisteniService(PojisteniRepository pojisteniRepository, PojistenecRepository repository, PojistenecService service) {
        this.pojisteniRepository = pojisteniRepository;
        this.repository = repository;  // Injektování PojistenecRepository
        this.service = service; // Injektování PojistenecService

        if (pojisteniRepository == null) {
            System.out.println("PojisteniRepository is NULL");
        } else {
            System.out.println("PojisteniRepository injected successfully.");
        }
    }

    // Zobrazí stránku všech pojištění
    public Page<Pojisteni> dejStranu(Pageable pageable) {
        return pojisteniRepository.findAll(pageable);  // Používá vestavěnou metodu z JpaRepository
    }

    // Vyhledá pojištění podle jména pojištěnce (s podporou stránkování)
    public Page<Pojisteni> najdiStranu(String query, Pageable pageable) {
        // Předpokládá se, že máte metodu v repository pro hledání podle jména pojištěnce
        return pojisteniRepository.findByPojistenecNameContaining(query, pageable);
    }

    // Získá všechna pojištění pro konkrétního pojištěnce podle jeho ID
    public List<Pojisteni> getAllPojisteniForPojistenec(Long id) {
        return pojisteniRepository.findByPojistenecId(id);
    }

    // Získá konkrétní pojištění podle jeho ID
    public Pojisteni dejPojisteniPodleId(Long idPojisteni) {
        return pojisteniRepository.findById(idPojisteni)
                .orElseThrow(() -> new EntityNotFoundException("Pojisteni with ID " + idPojisteni + " not found"));
    }

    // Přidá nové pojištění pro konkrétního pojištěnce
    public void pridejPojisteni(Long id, Pojisteni pojisteni) {
        // Získáme pojištěnce podle ID
        Pojistenec pojistenec = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pojistenec ID"));

        // Nastavíme pojištěnce na pojištění
        pojisteni.setPojistenec(pojistenec);

        // Uložíme pojištění do databáze
        if (pojisteniRepository == null) {
            throw new IllegalStateException("PojisteniRepository is not injected.");
        }
        pojisteniRepository.save(pojisteni);
    }

    // Aktualizuje pojištění
    public void upravPojisteni(Long id, Pojisteni updatedPojisteni) {
        // Zajistíme, že pojištění existuje, než ho budeme upravovat
        Pojisteni pojisteni = pojisteniRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pojisteni not found"));

        // Aktualizujeme hodnoty pojištění
        pojisteni.setTypPojisteni(updatedPojisteni.getTypPojisteni());
        pojisteni.setCastka(updatedPojisteni.getCastka());
        pojisteni.setPredmetPojisteni(updatedPojisteni.getPredmetPojisteni());

        // Pokud jsou hodnoty datumOd a datumDo nastavené, aktualizujeme je
        if (updatedPojisteni.getDatumOd() != null) {
            pojisteni.setDatumOd(updatedPojisteni.getDatumOd());
        }

        if (updatedPojisteni.getDatumDo() != null) {
            pojisteni.setDatumDo(updatedPojisteni.getDatumDo());
        }

        // Uložíme aktualizované pojištění do databáze
        pojisteniRepository.save(pojisteni);
    }

    // Smaže pojištění podle jeho ID
    public void smazPojisteni(Long idPojisteni) {
        pojisteniRepository.deleteById(idPojisteni);  // Používá repository pro smazání pojištění
    }
}
