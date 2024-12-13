package cz.example.Pojistenci.service;

import cz.example.Pojistenci.exceptions.ResourceNotFoundException;
import cz.example.Pojistenci.models.Pojistenec;
import cz.example.Pojistenci.models.Pojisteni;
import cz.example.Pojistenci.repositories.PojistenecRepository;
import cz.example.Pojistenci.repositories.PojisteniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PojistenecService {

    @Autowired
    private PojistenecRepository repository;  // Repository pro práci s entitou Pojistenec

    private PojisteniRepository pojisteniRepository;  // Repository pro práci s entitou Pojisteni
    private PojisteniService pojisteniService;  // Service pro manipulaci s pojištěním

    // Uloží pojištěnce do databáze
    public Pojistenec ulozPojistence(Pojistenec pojistenec) {
        return repository.save(pojistenec);
    }

    // Zobrazí všechny pojištěnce s podporou stránkování
    public Page<Pojistenec> zobrazVsechnyPojistence(Pageable pageable) {
        return repository.findAll(pageable); // Používá vestavěnou metodu z JpaRepository



}

    // Hledá pojištěnce podle jména a příjmení
    public List<Pojistenec> hledejPojistence(String query) {

        String[] queryParts = query.split("\\s+");
        String jmenoQuery = queryParts.length > 0 ? queryParts[0] : "";
        String prijmeniQuery = queryParts.length > 1 ? queryParts[1] : "";

        if (queryParts.length == 1) {
            return repository.findByJmenoStartingWithOrPrijmenniStartingWith(query, query);
        }

        return repository.findByJmenoStartingWithAndPrijmenniStartingWith(jmenoQuery, prijmeniQuery);
    }



    // Vyhledání pojištěnců na základě jména a příjmení s podporou stránkování
    public Page<Pojistenec> najdiPojistence(String query, Pageable pageable) {

        String[] queryParts = query.split("\\s+");
        String jmenoQuery = queryParts.length > 0 ? queryParts[0] : "";
        String prijmeniQuery = queryParts.length > 1 ? queryParts[1] : "";

        if (queryParts.length == 1) {
            return repository.findByJmenoStartingWithOrPrijmenniStartingWith(query, query, pageable);
        }

        return repository.findByJmenoStartingWithAndPrijmenniStartingWith(jmenoQuery, prijmeniQuery, pageable);
    }




    // Vrací pojištěnce podle jeho ID
    public Pojistenec dejPojistencePodleId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pojistenec not found with id: " + id));
    }

    // Smaže pojištěnce podle jeho ID
    public void smazPojistence(Long id) {
        repository.deleteById(id);  // Používá repository pro smazání pojištěnce
    }

    @Value("${upload.directory}")
    private String uploadDirectory;  // Cesta pro ukládání obrázků

    // Aktualizuje informace o pojištěnci, včetně obrázku
    public Pojistenec upravPojistence(Long id, String jmeno, String prijmenni, String telefonniCislo, int vek, String pohlavi, String email, String ulice, String mesto, String psc, MultipartFile image) throws IOException {
        Pojistenec pojistenec = dejPojistencePodleId(id);

        if (pojistenec != null) {
            // Nastaví hodnoty, které jsou skutečně poskytnuty a odlišné
            pojistenec.setJmeno(jmeno);
            pojistenec.setPrijmenni(prijmenni);
            pojistenec.setTelefonniCislo(telefonniCislo);
            pojistenec.setPohlavi(pohlavi);
            pojistenec.setEmail(email);
            pojistenec.setUlice(ulice);
            pojistenec.setMesto(mesto);
            pojistenec.setPsc(psc);

            // Pokud je poskytnut obrázek, uloží ho
            if (!image.isEmpty()) {
                String lokaceObrazku = ulozObrazek(image);
                pojistenec.setLokaceObrazku(lokaceObrazku);
            }

            // Uloží aktualizovaného pojištěnce
            return repository.save(pojistenec);
        }
        throw new RuntimeException("Pojištěnec not found");
    }

    // Uloží obrázek na server a vrátí jeho relativní cestu
    public String ulozObrazek(MultipartFile obrazek) throws IOException {
        String nazevObrazku = StringUtils.cleanPath(Objects.requireNonNull(obrazek.getOriginalFilename()));

        // Vytvoří cestu pro uložení obrázku (unikátní název založený na UUID)
        Path cilovaLokace = Paths.get("src/main/resources/static/images/" + UUID.randomUUID().toString() + "_" + nazevObrazku);

        // Ujistí se, že rodičovský adresář existuje
        Files.createDirectories(cilovaLokace.getParent());

        // Zkopíruje obrázek na požadované místo
        Files.copy(obrazek.getInputStream(), cilovaLokace, StandardCopyOption.REPLACE_EXISTING);

        // Vrátí relativní cestu, která bude použita v URL
        return "/images/" + cilovaLokace.getFileName().toString();
    }

    // Přidá pojištění k pojištěnci
    public void pridejPojisteni(Long pojistenecId, Pojisteni pojisteni) {
        Pojistenec pojistenec = repository.findById(pojistenecId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid pojistenec ID"));

        // Nastaví pojištěnce na pojištění
        pojisteni.setPojistenec(pojistenec);

        // Přidá pojištění k pojištěnci
        pojistenec.addPojisteni(pojisteni);

        // Uloží pojištění
        pojisteniRepository.save(pojisteni);

        // Volitelně, uloží aktualizovaného pojištěnce
        repository.save(pojistenec);
    }
}
