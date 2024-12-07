package cz.example.Pojistenci.controllers;

import cz.example.Pojistenci.models.Pojistenec;
import cz.example.Pojistenci.models.Pojisteni;
import cz.example.Pojistenci.repositories.PojistenecRepository;
import cz.example.Pojistenci.repositories.PojisteniRepository;
import cz.example.Pojistenci.service.PojistenecService;
import cz.example.Pojistenci.service.PojisteniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class PojisteniController {

    @Autowired
    private PojisteniService pojisteniService;  // Služba pro práci s pojištěními

    @Autowired
    private PojistenecService service;  // Služba pro práci s pojištěnci

    @Autowired
    private PojistenecRepository repository;  // Repozitář pro pojištěnce

    @Autowired
    private PojisteniRepository pojisteniRepository;  // Repozitář pro pojištění


    // Metoda pro zobrazení seznamu pojištění s podporou stránkování a vyhledávání
    @GetMapping("/pojisteni/seznamPojisteni")
    public String dejSeznamPojisteni(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(required = false) String query, // Parametr pro vyhledávání
                                     Model model) {
        int pageSize = 5;  // Počet položek na stránce
        Pageable pageable = PageRequest.of(page - 1, pageSize);  // Nastavení stránkování pomocí Spring Pageable

        // Načtení pojištění na základě vyhledávacího dotazu nebo bez něj
        Page<Pojisteni> stranaPojisteni = (query != null && !query.isEmpty()) ?
                pojisteniService.najdiStranu(query, pageable) :
                pojisteniService.dejStranu(pageable);

        // Přidání seznamu pojištění do modelu
        model.addAttribute("pojisteni", stranaPojisteni.getContent());
        model.addAttribute("celkemStran", stranaPojisteni.getTotalPages());  // Celkový počet stran
        model.addAttribute("soucasnaStrana", page);  // Aktuální stránka
        model.addAttribute("vstup", query);  // Parametr pro vyhledávání

        return "seznamPojisteni";  // Název šablony pro zobrazení seznamu pojištění
    }


    // Metoda pro přidání pojištění pro konkrétního pojištěnce
    @GetMapping("/pojistenec/{id}/pridatPojisteni")
    public String pridatPojisteni(@PathVariable Long id, Model model) {
        // Načtení pojištěnce podle ID a přidání do modelu
        Optional<Pojistenec> pojistenec = Optional.ofNullable(service.dejPojistencePodleId(id));
        if (pojistenec.isPresent()) {
            model.addAttribute("pojistenec", pojistenec.get());
        } else {
            // Pokud pojištěnec není nalezen, přesměrování na chybovou stránku
            return "redirect:/error";  // Přesměrování na stránku s chybou
        }
        return "pridatPojisteni";  // Název šablony pro přidání pojištění
    }


    // Nastavení formátu data pro binding
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), false));
    }


    // Metoda pro uložení nového pojištění při odeslání formuláře
    @PostMapping("/pojistenec/{id}/pridatPojisteni")
    public String pridatPojisteni(@PathVariable Long id, @ModelAttribute Pojisteni pojisteni, RedirectAttributes redirectAttributes) {
        // Načtení pojištěnce podle ID
        Pojistenec pojistenec = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nevalidní ID pro pojištěnce"));

        // Nastavení pojištěnce pro pojištění
        pojisteni.setPojistenec(pojistenec);

        // Uložení pojištění do databáze
        pojisteniRepository.save(pojisteni);

        // Přidání flash zprávy o úspěchu
        redirectAttributes.addFlashAttribute("successMessage", "Nové pojištění bylo úspěšně přidáno do databáze.");

        // Přesměrování na detail pojištěnce
        return "redirect:/pojistenec/pojistenecDetail/" + id;  // Přesměrování na stránku detailu pojištěnce
    }


    // Metoda pro zobrazení detailu pojištění
    @GetMapping("/pojisteni/pojisteniDetail/{idPojisteni}")
    public String pojisteniDetail(@PathVariable Long idPojisteni, Model model) {
        // Načtení pojištění podle ID
        Pojisteni pojisteni = pojisteniService.dejPojisteniPodleId(idPojisteni);

        // Pokud pojištění není nalezeno, přesměrování na stránku s chybou
        if (pojisteni == null) {
            System.out.println("No Pojisteni found with ID: " + idPojisteni);
            return "redirect:/error";  // Přesměrování na stránku s chybou
        }

        // Formátování datumu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. M. yyyy");
        String datumOdFormatted = (pojisteni.getDatumOd() != null)
                ? pojisteni.getDatumOd().format(formatter)
                : "Not Available";
        String datumDoFormatted = (pojisteni.getDatumDo() != null)
                ? pojisteni.getDatumDo().format(formatter)
                : "Not Available";

        // Přidání pojištění a formátovaných datumů do modelu
        model.addAttribute("pojisteni", pojisteni);
        model.addAttribute("datumOdFormatted", datumOdFormatted);
        model.addAttribute("datumDoFormatted", datumDoFormatted);
        model.addAttribute("pojistenec", pojisteni.getPojistenec());

        // Návrat na šablonu detailu pojištění
        return "pojisteniDetail";  // Šablona pro zobrazení detailu pojištění
    }


    // Zobrazení formuláře pro úpravu pojištění
    @GetMapping("/upravitPojisteni/{idPojisteni}")
    public String dejPridatPojisteni(@PathVariable("idPojisteni") Long idPojisteni, Model model) {
        // Načtení pojištění podle ID
        Pojisteni pojisteni = pojisteniService.dejPojisteniPodleId(idPojisteni);
        model.addAttribute("pojisteni", pojisteni);  // Přidání pojištění do modelu
        return "upravitPojisteni";  // Šablona pro úpravu pojištění
    }

    // Metoda pro odeslání formuláře a aktualizaci pojištění
    @PostMapping("/update/{idPojisteni}")
    public String upravPojisteni(
            @PathVariable("idPojisteni") Long idPojisteni,
            @ModelAttribute("pojisteni") Pojisteni pojisteni) {

        System.out.println("Received datumOd: " + pojisteni.getDatumOd());
        System.out.println("Received datumDo: " + pojisteni.getDatumDo());

        // Aktualizace pojištění v databázi
        pojisteniService.upravPojisteni(idPojisteni, pojisteni);

        // Přesměrování na detail pojištění po úspěšné aktualizaci
        return "redirect:/pojisteni/pojisteniDetail/" + idPojisteni;
    }

    // Metoda pro smazání pojištění
    @PostMapping("/delete/{idPojisteni}")
    public String smazPojisteni(@PathVariable Long idPojisteni, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Volání služby pro smazání pojištění podle ID
            pojisteniService.smazPojisteni(idPojisteni);

            // Přidání zprávy o úspěchu do modelu
            model.addAttribute("message", "Pojištění bylo úspěšně odstraněno.");
        } catch (Exception e) {
            // Ošetření chyby při mazání pojištění
            model.addAttribute("error", "Chyba při odstraňování pojištění.");
        }

        // Přidání flash zprávy o úspěchu
        redirectAttributes.addFlashAttribute("successMessage", "Pojištění bylo smazáno.");

        // Přesměrování zpět na seznam pojištění
        return "redirect:/pojisteni/seznamPojisteni";  // Návrat na seznam pojištění
    }
}
