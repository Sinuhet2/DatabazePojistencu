# Databáze pojištěnců - Webová aplikace pro správu pojištěnců

## Popis projektu

Tato aplikace umožňuje správu seznamu pojištěnců a jejich pojištění, včetně přidávání, upravování a mazání záznamů. Je vytvořena pomocí Spring Boot, Thymeleaf, Bootstrap a databáze MariaDB.

## Požadavky

- **Java 17** nebo novější
- **Maven 3.8+**
- **MariaDB/MySQL** (není nutné ručně vytvářet databázi)
- **Git** pro klonování projektu

## Instalace a spuštění

1. **Klonování repozitáře**

   ```bash
   git clone https://github.com/Sinuhet2/DatabazePojistencu.git
   cd pojistenci
   ```

2. **Vytvoření souboru .env**

   Vytvořte soubor `.env` v kořenovém adresáři projektu a nastavte následující proměnné prostředí:

   ```plaintext
   DB_URL=jdbc:mariadb://localhost:3306/pojistenci
   USERNAME=vasUzivatel
   PASSWORD=vaseHeslo
   ```

3. **Spuštění aplikace**

   Použijte příkaz Maven:

   ```bash
   mvn spring-boot:run
   ```

4. **Přístup k aplikaci**

   Otevřete webový prohlížeč a přejděte na:

   ```
   http://localhost:8080
   ```

## Databáze

- **Automatické vytvoření:** Při spuštění aplikace se automaticky vytvoří databáze díky souboru `data.sql`.

- **Přizpůsobení:** Pokud potřebujete změnit nastavení databáze, upravte proměnné v souboru `.env`.

## Struktura projektu

- **src/main/java:** Hlavní soubor, kontrolery, služby, repozitáře, entity, konfigurace
- **src/main/resources/templates:** HTML šablony&#x20;
- **src/main/resources/static:** Obrázky
- **src/main/resources/data.sql:** Inicializační data pro databázi

## Technologie

- **Backend:** Spring Boot (Java)
- **Frontend:** Thymeleaf, Bootstrap
- **Databáze:** MariaDB/MySQL

## Autor

Vojtěch Černý

