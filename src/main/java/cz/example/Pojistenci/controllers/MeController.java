package cz.example.Pojistenci.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MeController {

    // Hodnota pro adresář pro nahrávání souborů bude vložena z konfigurace
    @Value("${upload.directory}")
    private String slozkaProNahravani;  // Tento atribut bude naplněn hodnotou z konfiguračního souboru

    // Metoda pro zobrazení stránky "O mně"
    @GetMapping("/oMne")
    public ModelAndView oMne() {
        return new ModelAndView("oMne.html");  // Vrací šablonu oMne.html
    }

    // Metoda pro zobrazení stránky "O projektu"
    @GetMapping("/oProjektu")
    public ModelAndView oProjektu() {
        return new ModelAndView("oProjektu.html");  // Vrací šablonu oProjektu.html
    }

}
