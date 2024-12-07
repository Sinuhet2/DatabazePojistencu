package cz.example.Pojistenci.exceptions;

// Tato třída reprezentuje vlastní výjimku pro případ, kdy zdroj (např. pojištěnec nebo pojištění) není nalezen.
public class ResourceNotFoundException extends RuntimeException {

    // Konstruktor výjimky, který přijímá zprávu o chybě
    public ResourceNotFoundException(String message) {
        super(message);  // Předání zprávy do konstruktoru základní třídy RuntimeException
    }
}
