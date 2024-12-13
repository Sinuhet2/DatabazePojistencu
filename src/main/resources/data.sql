-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Počítač: localhost
-- Vytvořeno: Ned 08. pro 2024, 17:48
-- Verze serveru: 10.4.28-MariaDB
-- Verze PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáze: `pojistenci`
--

-- --------------------------------------------------------

--
-- Struktura tabulky `pojistenec`
--

CREATE TABLE `pojistenec` (
  `id` bigint(20) NOT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `jmeno` varchar(255) DEFAULT NULL,
  `pohlavi` varchar(255) DEFAULT NULL,
  `prijmenni` varchar(255) DEFAULT NULL,
  `telefonni_cislo` varchar(255) DEFAULT NULL,
  `mesto` varchar(255) DEFAULT NULL,
  `psc` varchar(255) DEFAULT NULL,
  `ulice` varchar(255) DEFAULT NULL,
  `lokace_obrazku` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Vypisuji data pro tabulku `pojistenec`
--

INSERT INTO `pojistenec` (`id`, `bio`, `email`, `image_path`, `jmeno`, `pohlavi`, `prijmenni`, `telefonni_cislo`, `mesto`, `psc`, `ulice`, `lokace_obrazku`) VALUES
(36, NULL, 'marek@sparklehorse.cz', '/images/90c17f2b-2f9c-4d5b-af51-76b20bdcdb27_S.png', 'Marek', 'muž', 'Linka', '123 123 123', 45, 'Praha 17', '11000', 'Koňský trh 21', '/images/7939b1ae-2f27-495b-b47c-ab045a3d1aa5_S.png'),
(37, NULL, 'either@or.com', '/images/23bb4cdf-bcb4-46b7-af5a-25c98e63b6ef_ab6761610000e5eb079739b801ab3f105866b76f.jpeg', 'Eliáš', 'muž', 'Novák', '234 234 232', 32, 'Praha 23', '66000', 'Kondorská 22', '/images/67be31ab-fd61-4d49-a55c-cf537bee2f58_ab6761610000e5eb079739b801ab3f105866b76f.jpeg'),
(38, NULL, 'billcallahan@smog.com', '/images/582de888-0bbd-4899-8446-2d0b16719a44_5f522e078d061Bill Callahan7gqsi6aBSkRMJoL9psKqMr.jpg', 'Vilém', 'muž', 'Kalina', '345 345 345', 46, 'Praha 31', '66600', 'Nuselská 44', '/images/ba466594-fa7d-4891-b524-c7cc9b1ecd68_5f522e078d061Bill Callahan7gqsi6aBSkRMJoL9psKqMr.jpg'),
(39, NULL, 'phil.microphone@mounteerie.com', '/images/65529198-8363-49ef-91db-b13725c3f403_2022_Mount+Eerie_glasses.jpg', 'Filip', 'muž', 'Elverumský', '543 666 543', 43, 'Praha 56', '54555', 'Strachohorská 33', '/images/56aaec14-aec5-49ac-a5ff-398c9033efb1_2022_Mount+Eerie_glasses.jpg'),
(78, NULL, 'cynthia.dahl@seznam.cz', NULL, 'Cyntie', 'žena', 'Dalová', '123 456 789', NULL, 'Praha 25', '12345', 'Na severu, 44', NULL),
(79, NULL, 'fiona.apple@gmail.cz', NULL, 'Filoména', 'žena', 'Jablková', '987 654 321', NULL, 'Praha 74', '54322', 'Jablečná 44', NULL),

-- --------------------------------------------------------

--
-- Struktura tabulky `pojisteni`
--

CREATE TABLE `pojisteni` (
  `id_pojisteni` bigint(20) NOT NULL,
  `castka` bigint(20) NOT NULL,
  `datum_do` date DEFAULT NULL,
  `datum_od` date DEFAULT NULL,
  `predmet_pojisteni` varchar(255) DEFAULT NULL,
  `typ_pojisteni` varchar(255) DEFAULT NULL,
  `pojistenec_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Vypisuji data pro tabulku `pojisteni`
--

INSERT INTO `pojisteni` (`id_pojisteni`, `castka`, `datum_do`, `datum_od`, `predmet_pojisteni`, `typ_pojisteni`, `pojistenec_id`) VALUES
(28, 600000, '2024-11-30', '2024-11-05', 'Kytara', 'majetku', 36),
(29, 100000, '2024-11-30', '2024-11-01', 'Motorka', 'majetku', 36),
(31, 10000, '2024-11-30', '2024-11-01', 'Kytara', 'majetku', 38),
(32, 10000, '2024-11-30', '2024-11-01', 'Cestování', 'zájmu', 39),
(43, 1000000, '2024-12-31', '2024-12-01', 'Zdravotní pojištění', 'osob', 38);

--
-- Indexy pro exportované tabulky
--

--
-- Indexy pro tabulku `pojistenec`
--
ALTER TABLE `pojistenec`
  ADD PRIMARY KEY (`id`);

--
-- Indexy pro tabulku `pojisteni`
--
ALTER TABLE `pojisteni`
  ADD PRIMARY KEY (`id_pojisteni`),
  ADD KEY `FK8mma2snuy42kcuwoby3m7ff5e` (`pojistenec_id`);

--
-- AUTO_INCREMENT pro tabulky
--

--
-- AUTO_INCREMENT pro tabulku `pojistenec`
--
ALTER TABLE `pojistenec`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=85;

--
-- AUTO_INCREMENT pro tabulku `pojisteni`
--
ALTER TABLE `pojisteni`
  MODIFY `id_pojisteni` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- Omezení pro exportované tabulky
--

--
-- Omezení pro tabulku `pojisteni`
--
ALTER TABLE `pojisteni`
  ADD CONSTRAINT `FK8mma2snuy42kcuwoby3m7ff5e` FOREIGN KEY (`pojistenec_id`) REFERENCES `pojistenec` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
