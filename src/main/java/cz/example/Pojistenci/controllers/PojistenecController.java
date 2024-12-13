package cz.example.Pojistenci.controllers;

import cz.example.Pojistenci.models.Pojistenec;
import cz.example.Pojistenci.models.Pojisteni;
import cz.example.Pojistenci.repositories.PojistenecRepository;
import cz.example.Pojistenci.service.PojistenecService;
import cz.example.Pojistenci.service.PojisteniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pojistenec")
public class PojistenecController {

    //Služba pro správu a logiku dat z jiných tříd
    private PojistenecService service;

    private PojistenecRepository repository;

    private PojisteniService pojisteniService;


    // Konstruktor pro PojistenecController, který injektuje potřebné třídy
    public PojistenecController(PojistenecService service, PojisteniService pojisteniService) {
        this.service = service;
        this.pojisteniService = pojisteniService;
    }

    // Kontroler pro zobrazení domovské stránky
    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index.html");
    }

    // Kontroler pro zobrazení seznamu pojištěnců
    @GetMapping("/seznamPojistencu")
    public String ukazSeznamPojistencu(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(required = false) String query,  // dotaz může být null, pokud není zadán
                                       Model model) {

        int velikostStrany = 5;  // Počet pojištěnců na jednu stranu
        Pageable pageable = PageRequest.of(page - 1, velikostStrany);

        Page<Pojistenec> stranaPojistencu;

        if (query != null && !query.trim().isEmpty()) {
            // Pokud je zadán dotaz, hledá všechny záznamy
            stranaPojistencu = service.najdiPojistence(query, pageable);
        } else {
            // Pokud není zadán dotaz, zobrazuje všechny záznamy
            stranaPojistencu = service.zobrazVsechnyPojistence(pageable);
        }

        model.addAttribute("pojistenci", stranaPojistencu.getContent());
        model.addAttribute("celkemStran", stranaPojistencu.getTotalPages());
        model.addAttribute("soucasnaStrana", page);
        model.addAttribute("vstup", query);
        return "seznamPojistencu";  // Návrat šablony pro seznam pojištěnců
    }

    // Kontroler pro vyhledání pojištěnců na základě dotazu
    @GetMapping("/seznamPojistencu/search")
    @ResponseBody
    public List<Pojistenec> hledejPojistence(@RequestParam String query) {
        List<Pojistenec> vysledky = service.hledejPojistence(query);
        return vysledky.isEmpty() ? new ArrayList<>() : vysledky;
    }

    // Zobrazení formuláře pro přidání nového pojištěnce
    @GetMapping("/pridatPojistence")
    public String dejPridatPojistence(Model model) {
        model.addAttribute("pojistenec", new Pojistenec());
        return "pridatPojistence";  // Zobrazí šablonu pro přidání pojištěnce
    }

    // Zpracování formuláře pro přidání nového pojištěnce
    @PostMapping("/pridatPojistence")
    public String pridejPojistence(@ModelAttribute Pojistenec pojistenec, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "pridatPojistence"; // Pokud jsou chyby, vrátí formulář zpět
        }

        service.ulozPojistence(pojistenec); // Uložení pojištěnce do databáze

        redirectAttributes.addFlashAttribute("successMessage", "Nový pojištěnec byl úspěšně přidán do databáze.");

        return "redirect:/pojistenec/seznamPojistencu"; // Přesměrování na stránku seznamu pojištěnců
    }

    @Value("${upload.directory}")
    private String slozkaProNahravani;  // Cesta pro nahrávání souborů, injektováno z konfigurace

    // Metoda pro vytvoření nového pojištěnce
    @PostMapping("/vytvor")
    public String vytvorPojistence(
            @RequestParam String jmeno,
            @RequestParam String prijmenni,
            @RequestParam String telefonniCislo,
            @RequestParam String pohlavi,
            @RequestParam String email,
            @RequestParam String ulice,
            @RequestParam String mesto,
            @RequestParam String psc,
            @RequestParam(required = false) MultipartFile obrazek) throws IOException {

        // Vytvoření nového objektu Pojistenec (entita)
        Pojistenec pojistenec = new Pojistenec();
        pojistenec.setJmeno(jmeno);
        pojistenec.setPrijmenni(prijmenni);
        pojistenec.setTelefonniCislo(telefonniCislo);
        pojistenec.setPohlavi(pohlavi);
        pojistenec.setEmail(email);
        pojistenec.setUlice(ulice);
        pojistenec.setMesto(mesto);
        pojistenec.setPsc(psc);

        // Zpracování obrázku, pokud je nahrán
        if (obrazek != null && !obrazek.isEmpty()) {
            // Ošetření názvu souboru pro zamezení problémů s nebezpečnými znaky
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(obrazek.getOriginalFilename()));

            // Definování cesty pro uložení obrázku
            Path targetLocation = Paths.get(slozkaProNahravani + "/" + fileName);

            // Zajištění, že složka pro upload existuje
            Files.createDirectories(targetLocation.getParent());

            // Uložení souboru na disk
            Files.copy(obrazek.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Nastavení relativní cesty k obrázku v entitě Pojistenec
            pojistenec.setLokaceObrazku("/images/" + fileName);
        }

        // Uložení pojištěnce do databáze
        repository.save(pojistenec);

        // Přesměrování na indexovou stránku (nebo jinou vhodnou stránku)
        return "redirect:/index";
    }

    // Zobrazení detailu pojištěnce
    @GetMapping("/pojistenecDetail/{id}")
    public String zobrazDetailPojistence(@PathVariable Long id, Model model) {
        Pojistenec pojistenec = service.dejPojistencePodleId(id);  // Získání pojištěnce podle ID
        model.addAttribute("pojisteni", pojisteniService.dejVsechnaPojisteniProPojistence(id));
        model.addAttribute("pojistenec", pojistenec);  // Předání pojištěnce do modelu pro zobrazení
        return "pojistenecDetail";  // Návrat šablony pro detail pojištěnce
    }

    // Metoda pro nahrání nového obrázku pro pojištěnce
    @PostMapping("/nacti/{id}")
    public String nahrajObrazek(@PathVariable Long id, @RequestParam("obrazek") MultipartFile obrazek) throws IOException {
        if (obrazek.isEmpty()) {
            return "redirect:/detail/" + id;  // Pokud není žádný obrázek, přesměruje zpět na detail
        }

        // Vytvoření cesty k souboru pro uložení
        String nazevSouboru = StringUtils.cleanPath(Objects.requireNonNull(obrazek.getOriginalFilename()));
        Path trasa = Paths.get(slozkaProNahravani + File.separator + nazevSouboru);

        // Zajištění existence složky před uložením
        Files.createDirectories(trasa.getParent());

        // Uložení obrázku do složky
        Files.copy(obrazek.getInputStream(), trasa, StandardCopyOption.REPLACE_EXISTING);

        // Aktualizace cesty k obrázku v entitě Pojistenec
        Pojistenec pojistenec = service.dejPojistencePodleId(id);
        pojistenec.setLokaceObrazku("/images/" + nazevSouboru);
        service.ulozPojistence(pojistenec);  // Uložení aktualizovaného pojištěnce

        // Přesměrování zpět na detail pojištěnce
        return "redirect:/pojistenec/detail/" + id;
    }

    // Získání nahraného obrázku (pro zobrazení)
    @GetMapping("/images/{nazevObrazku}")
    public ResponseEntity<Resource> dejObrazek(@PathVariable String nazevObrazku) throws IOException {
        Path lokaceSouboru = Paths.get(slozkaProNahravani + "/images/" + nazevObrazku);
        Resource resource = new FileSystemResource(lokaceSouboru.toFile());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();  // Pokud soubor neexistuje, vrátí 404
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);  // Vrátí obrázek ve formátu JPEG
    }

    // Metoda pro aktualizaci pojištěnce
    @PostMapping("/update/{id}")
    public String upravPojistence(@PathVariable("id") Long id,
                                  @RequestParam(value = "jmeno", required = false) String jmeno,
                                  @RequestParam(value = "prijmenni", required = false) String prijmenni,
                                  @RequestParam(value = "telefonniCislo", required = false) String telefonniCislo,
                                  @RequestParam(value = "pohlavi", required = false) String pohlavi,
                                  @RequestParam(value = "email", required = false) String email,
                                  @RequestParam(value = "ulice", required = false) String ulice,
                                  @RequestParam(value = "mesto", required = false) String mesto,
                                  @RequestParam(value = "psc", required = false) String psc,
                                  @RequestParam(value = "obrazek", required = false) MultipartFile obrazek,
                                  Model model) {

        Optional<Pojistenec> pojistenecOptional = Optional.ofNullable(service.dejPojistencePodleId(id));
        if (!pojistenecOptional.isPresent()) {
            model.addAttribute("error", "Pojistenec nenalezen.");
            return "redirect:/error";
        }

        Pojistenec pojistenec = pojistenecOptional.get();

        // Aktualizace polí pouze pokud byly nové hodnoty zadány
        if (jmeno != null && !jmeno.isEmpty()) pojistenec.setJmeno(jmeno);
        if (prijmenni != null && !prijmenni.isEmpty()) pojistenec.setPrijmenni(prijmenni);
        if (telefonniCislo != null && !telefonniCislo.isEmpty()) pojistenec.setTelefonniCislo(telefonniCislo);
        if (pohlavi != null && !pohlavi.isEmpty()) pojistenec.setPohlavi(pohlavi);
        if (email != null && !email.isEmpty()) pojistenec.setEmail(email);
        if (ulice != null && !ulice.isEmpty()) pojistenec.setUlice(ulice);
        if (mesto != null && !mesto.isEmpty()) pojistenec.setMesto(mesto);
        if (psc != null && !psc.isEmpty()) pojistenec.setPsc(psc);

        // Aktualizace obrázku, pokud je nahrán nový obrázek
        if (obrazek != null && !obrazek.isEmpty()) {
            try {
                String lokaceObrazku = service.ulozObrazek(obrazek); // Uložení obrázku přes service
                pojistenec.setLokaceObrazku(lokaceObrazku);  // Nastavení cesty k obrázku v entitě
            } catch (Exception e) {
                model.addAttribute("error", "Obrázek se nepodařilo nahrát.");
                return "error";
            }
        }

        // Uložení aktualizovaného pojištěnce
        service.ulozPojistence(pojistenec);

        // Přesměrování zpět na detail pojištěnce
        return "redirect:/pojistenec/pojistenecDetail/" + id;
    }

    // Metoda pro smazání pojištěnce
    @PostMapping("/delete/{id}")
    public String smazPojistence(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Smazání pojištěnce podle ID
        service.smazPojistence(id);

        redirectAttributes.addFlashAttribute("successMessage", "Pojištěnec byl smazán.");

        return "redirect:/pojistenec/seznamPojistencu"; // Přesměrování po smazání zpět na seznam pojištěnců
    }

    // Zobrazení formuláře pro přidání pojištění pro pojištěnce
    @GetMapping("/pojistenec/pridatPojisteni/{id}")
    public String pridatPojisteni(@PathVariable Long id, Model model) {
        Optional<Pojistenec> pojistenec = Optional.ofNullable(service.dejPojistencePodleId(id));
        if (pojistenec.isPresent()) {
            model.addAttribute("pojistenec", pojistenec.get());
        } else {
            return "redirect:/error";  // Chyba, pokud pojištěnec není nalezen
        }
        return "pridatPojisteni";  // Zobrazí šablonu pro přidání pojištění
    }

    // Zpracování přidání pojištění
    @PostMapping("/pojistenec/pridatPojisteni/{id}")
    public String pridatPojisteniOdeslat(@PathVariable Long id, @ModelAttribute Pojisteni pojisteni) {
        pojisteniService.pridejPojisteni(id, pojisteni);
        return "redirect:/pojistenec/pojistenecDetail/" + id;  // Přesměrování na detail pojištěnce
    }

    // Editace pojištění pro pojištěnce
    @GetMapping("/pojistenec/{id}/editPojisteni")
    public String upravPojisteni(@PathVariable Long id, @RequestParam Long pojisteniId, Model model) {
        // Načtení pojištěnce podle ID
        Optional<Pojistenec> optionalPojistenec = Optional.ofNullable(service.dejPojistencePodleId(id));

        if (optionalPojistenec.isPresent()) {
            Pojistenec pojistenec = optionalPojistenec.get();
            // Načtení pojištění podle ID
            Pojisteni pojisteni = pojisteniService.dejPojisteniPodleId(pojisteniId);

            // Předání dat do modelu
            model.addAttribute("pojistenec", pojistenec);
            model.addAttribute("pojisteni", pojisteni);
        } else {
            // Pokud pojištěnec není nalezen
            model.addAttribute("error", "Pojištěnec nenalezen.");
        }

        return "editPojisteni";  // Zobrazí šablonu pro úpravu pojištění
    }

    // Zobrazení formuláře pro úpravu pojištěnce
    @GetMapping("/upravitPojistence/{id}")
    public String pridatPojistencePage(@PathVariable Long id, Model model) {
        Pojistenec pojistenec = service.dejPojistencePodleId(id);
        model.addAttribute("pojistenec", pojistenec);  // Předání pojištěnce do modelu pro úpravu
        return "upravitPojistence";  // Zobrazí šablonu pro úpravu pojištěnce
    }
}
