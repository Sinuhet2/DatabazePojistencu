package cz.example.Pojistenci.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

// Třída reprezentující pojištění (pojistnou smlouvu) pro pojištěnce
@Entity
public class Pojisteni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPojisteni;  // Unikátní identifikátor pojištění

    private String typPojisteni;  // Typ pojištění (např. životní pojištění, majetkové pojištění)
    private long castka;  // Pojištěná částka
    private String predmetPojisteni;  // Předmět pojištění (např. dům, auto, zdraví)
    private LocalDate datumOd;  // Datum začátku pojištění
    private LocalDate datumDo;  // Datum konce pojištění

    // Relace Many-to-One mezi pojištěním a pojištěncem. Každé pojištění má jednoho pojištěnce.
    @ManyToOne
    @JoinColumn(name = "pojistenec_id", nullable = false)  // Sloupec pro uložení ID pojištěnce
    @JsonBackReference  // Zabraňuje nekonečné serializaci ve vztahu mezi pojištěním a pojištěncem
    private Pojistenec pojistenec;  // Pojištěnec (osoba, která má toto pojištění)

    // Výchozí konstruktor (povinný pro JPA)
    public Pojisteni() {
    }

    // Konstruktor pro inicializaci pojištění
    public Pojisteni(String typPojisteni, long castka, String predmetPojisteni, LocalDate datumOd, LocalDate datumDo) {
        this.typPojisteni = typPojisteni;
        this.castka = castka;
        this.predmetPojisteni = predmetPojisteni;
        this.datumOd = datumOd;
        this.datumDo = datumDo;
    }

    // Gettery a settery pro všechny atributy pojištění

    public String getTypPojisteni() {
        return typPojisteni;
    }

    public void setTypPojisteni(String typPojisteni) {
        this.typPojisteni = typPojisteni;
    }

    public long getCastka() {
        return castka;
    }

    public void setCastka(long castka) {
        this.castka = castka;
    }

    public String getPredmetPojisteni() {
        return predmetPojisteni;
    }

    public void setPredmetPojisteni(String predmetPojisteni) {
        this.predmetPojisteni = predmetPojisteni;
    }

    public LocalDate getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(LocalDate datumOd) {
        this.datumOd = datumOd;
    }

    public LocalDate getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(LocalDate datumDo) {
        this.datumDo = datumDo;
    }

    public Long getIdPojisteni() {
        return idPojisteni;
    }

    public void setIdPojisteni(Long idPojisteni) {
        this.idPojisteni = idPojisteni;
    }

    public Pojistenec getPojistenec() {
        return pojistenec;
    }

    public void setPojistenec(Pojistenec pojistenec) {
        this.pojistenec = pojistenec;
    }

    // Přepis metody toString pro zobrazení informací o pojištění
    @Override
    public String toString() {
        return "Pojisteni{" +
                "idPojisteni=" + idPojisteni +
                ", typPojisteni='" + typPojisteni + '\'' +
                ", castka=" + castka +
                ", predmetPojisteni='" + predmetPojisteni + '\'' +
                ", datumOd=" + (datumOd != null ? datumOd.toString() : "null") +
                ", datumDo=" + (datumDo != null ? datumDo.toString() : "null") +
                ", pojistenec=" + (pojistenec != null ? pojistenec.getId() : "null") + // Vytahuje ID pojištěnce
                '}';
    }
}
