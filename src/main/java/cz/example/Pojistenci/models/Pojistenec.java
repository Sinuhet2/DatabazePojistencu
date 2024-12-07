package cz.example.Pojistenci.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// Třída reprezentující pojištěnce (osoba, která má pojištění).
@Entity
public class Pojistenec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Identifikátor pojištěnce

    private String jmeno;  // Jméno pojištěnce
    private String prijmenni;  // Příjmení pojištěnce
    private String telefonniCislo;  // Telefonní číslo pojištěnce
    private String pohlavi;  // Pohlaví pojištěnce (muž, žena, jiné)
    private String email;  // Emailová adresa pojištěnce
    private String ulice;  // Ulice pojištěnce
    private String mesto;  // Město pojištěnce
    private String psc;  // PSČ pojištěnce
    private String lokaceObrazku;  // Lokace obrázku (např. profilový obrázek)

    // Relace mezi pojištěncem a pojištěním (One-to-Many). Jeden pojištěnec může mít více pojištění.
    @OneToMany(mappedBy = "pojistenec", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Pojisteni> pojisteni;  // Seznam pojištění spojených s pojištěncem

    // Výchozí konstruktor pro JPA (Java Persistence API)
    public Pojistenec() {}

    // Metoda pro přidání pojištění k pojištěnci
    public void addPojisteni(Pojisteni pojisteni) {
        if (pojisteni != null) {
            this.pojisteni.add(pojisteni);  // Přidání pojištění do seznamu
            pojisteni.setPojistenec(this);  // Nastavení oboustranné relace mezi pojištěním a pojištěncem
        }
    }

    // Konstruktor s parametry pro inicializaci pojištěnce
    public Pojistenec(String jmeno, String prijmenni, String telefonniCislo, String pohlavi, String email, String ulice, String mesto, String psc, String lokaceObrazku) {
        this.jmeno = jmeno;
        this.prijmenni = prijmenni;
        this.telefonniCislo = telefonniCislo;
        this.pohlavi = pohlavi;
        this.email = email;
        this.ulice = ulice;
        this.mesto = mesto;
        this.psc = psc;
        this.lokaceObrazku = lokaceObrazku;
    }

    // Gettery a settery pro všechny atributy pojištěnce

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmenni() {
        return prijmenni;
    }

    public void setPrijmenni(String prijmenni) {
        this.prijmenni = prijmenni;
    }

    public String getTelefonniCislo() {
        return telefonniCislo;
    }

    public void setTelefonniCislo(String telefonniCislo) {
        this.telefonniCislo = telefonniCislo;
    }

    public String getPohlavi() {
        return pohlavi;
    }

    public void setPohlavi(String pohlavi) {
        this.pohlavi = pohlavi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUlice() {
        return ulice;
    }

    public void setUlice(String ulice) {
        this.ulice = ulice;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public String getLokaceObrazku() {
        return lokaceObrazku;
    }

    public void setLokaceObrazku(String lokaceObrazku) {
        this.lokaceObrazku = lokaceObrazku;
    }

    public List<Pojisteni> getPojisteni() {
        return pojisteni;
    }

    public void setPojisteni(List<Pojisteni> pojisteni) {
        this.pojisteni = pojisteni;
    }

    // Přepis metody toString pro zobrazení informací o pojištěnci ve formátu textu
    @Override
    public String toString() {
        return String.format("%s %s Telefonní číslo: %s, Pohlaví: %s, Email: %s, Ulice a číslo popisné: %s, Město: %s, PSČ: %s", jmeno, prijmenni, telefonniCislo, pohlavi, email, ulice, mesto, psc);
    }
}
