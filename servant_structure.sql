-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Erstellungszeit: 29. Jun 2019 um 15:22
-- Server-Version: 5.6.34-log
-- PHP-Version: 7.1.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `servant_structure`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `autorole`
--

CREATE TABLE `autorole` (
  `guild_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `emote`
--

CREATE TABLE `emote` (
  `emote_name` varchar(32) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `feature_count`
--

CREATE TABLE `feature_count` (
  `id` bigint(18) NOT NULL,
  `feature` varchar(32) NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `guild_settings`
--

CREATE TABLE `guild_settings` (
  `guild_id` bigint(18) NOT NULL,
  `setting` varchar(32) NOT NULL,
  `value` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `interaction`
--

CREATE TABLE `interaction` (
  `interaction` varchar(32) NOT NULL,
  `gif` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `interaction_count`
--

CREATE TABLE `interaction_count` (
  `user_id` bigint(18) NOT NULL,
  `interaction` varchar(32) NOT NULL,
  `shared` int(11) NOT NULL,
  `received` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `join_notifier`
--

CREATE TABLE `join_notifier` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `mediaonlychannel`
--

CREATE TABLE `mediaonlychannel` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `toggle`
--

CREATE TABLE `toggle` (
  `guild_id` bigint(18) NOT NULL,
  `feature` varchar(32) NOT NULL,
  `is_enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_exp`
--

CREATE TABLE `user_exp` (
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `exp` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_settings`
--

CREATE TABLE `user_settings` (
  `user_id` bigint(18) NOT NULL,
  `setting` varchar(32) NOT NULL,
  `value` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `autorole`
--
ALTER TABLE `autorole`
  ADD PRIMARY KEY (`guild_id`,`role_id`);

--
-- Indizes für die Tabelle `emote`
--
ALTER TABLE `emote`
  ADD PRIMARY KEY (`emote_name`);

--
-- Indizes für die Tabelle `feature_count`
--
ALTER TABLE `feature_count`
  ADD PRIMARY KEY (`id`,`feature`);

--
-- Indizes für die Tabelle `guild_settings`
--
ALTER TABLE `guild_settings`
  ADD PRIMARY KEY (`guild_id`,`setting`);

--
-- Indizes für die Tabelle `interaction_count`
--
ALTER TABLE `interaction_count`
  ADD PRIMARY KEY (`user_id`,`interaction`);

--
-- Indizes für die Tabelle `join_notifier`
--
ALTER TABLE `join_notifier`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `mediaonlychannel`
--
ALTER TABLE `mediaonlychannel`
  ADD PRIMARY KEY (`guild_id`,`channel_id`);

--
-- Indizes für die Tabelle `toggle`
--
ALTER TABLE `toggle`
  ADD PRIMARY KEY (`guild_id`,`feature`);

--
-- Indizes für die Tabelle `user_exp`
--
ALTER TABLE `user_exp`
  ADD PRIMARY KEY (`user_id`,`guild_id`);

--
-- Indizes für die Tabelle `user_settings`
--
ALTER TABLE `user_settings`
  ADD PRIMARY KEY (`user_id`,`setting`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
