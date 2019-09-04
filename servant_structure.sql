-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Erstellungszeit: 04. Sep 2019 um 20:12
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
-- Tabellenstruktur für Tabelle `achievement`
--

CREATE TABLE `achievement` (
  `user_id` bigint(18) NOT NULL,
  `achievement` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `ap` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `alarm`
--

CREATE TABLE `alarm` (
  `user_id` bigint(18) NOT NULL,
  `alarm_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `autorole`
--

CREATE TABLE `autorole` (
  `guild_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `delay` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `best_of_image`
--

CREATE TABLE `best_of_image` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `number` int(11) NOT NULL,
  `percentage` int(11) NOT NULL,
  `emoji` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `best_of_image_bl`
--

CREATE TABLE `best_of_image_bl` (
  `message_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `best_of_quote`
--

CREATE TABLE `best_of_quote` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `number` int(11) NOT NULL,
  `percentage` int(11) NOT NULL,
  `emoji` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `best_of_quote_bl`
--

CREATE TABLE `best_of_quote_bl` (
  `message_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `birthdays`
--

CREATE TABLE `birthdays` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `birthday` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `birthday_gratulation`
--

CREATE TABLE `birthday_gratulation` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `was_gratulated` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `birthday_messages`
--

CREATE TABLE `birthday_messages` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `emote`
--

CREATE TABLE `emote` (
  `emote_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `feature_count`
--

CREATE TABLE `feature_count` (
  `id` bigint(18) NOT NULL,
  `feature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `giveawaylist`
--

CREATE TABLE `giveawaylist` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `host_id` bigint(18) NOT NULL,
  `prize` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `guild`
--

CREATE TABLE `guild` (
  `guild_id` bigint(18) NOT NULL,
  `prefix` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `offset` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `birthday_channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `image`
--

CREATE TABLE `image` (
  `image_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `interaction`
--

CREATE TABLE `interaction` (
  `interaction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gif` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `interaction_count`
--

CREATE TABLE `interaction_count` (
  `user_id` bigint(18) NOT NULL,
  `interaction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
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
-- Tabellenstruktur für Tabelle `leave_notifier`
--

CREATE TABLE `leave_notifier` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `level_role`
--

CREATE TABLE `level_role` (
  `guild_id` bigint(18) NOT NULL,
  `level` int(11) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `lobby`
--

CREATE TABLE `lobby` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
-- Tabellenstruktur für Tabelle `reaction_role`
--

CREATE TABLE `reaction_role` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `emoji` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `reminder`
--

CREATE TABLE `reminder` (
  `user_id` bigint(18) NOT NULL,
  `reminder_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `topic` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `streamers`
--

CREATE TABLE `streamers` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `streamer_mode`
--

CREATE TABLE `streamer_mode` (
  `guild_id` bigint(18) NOT NULL,
  `is_streamer_mode` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `streamhidden`
--

CREATE TABLE `streamhidden` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `streaming_role`
--

CREATE TABLE `streaming_role` (
  `guild_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `stream_channel`
--

CREATE TABLE `stream_channel` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `toggle`
--

CREATE TABLE `toggle` (
  `guild_id` bigint(18) NOT NULL,
  `feature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `user_id` bigint(18) NOT NULL,
  `offset` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `prefix` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
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
  `setting` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `value` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user_votes`
--

CREATE TABLE `user_votes` (
  `message_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `votes`
--

CREATE TABLE `votes` (
  `message_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `achievement`
--
ALTER TABLE `achievement`
  ADD PRIMARY KEY (`user_id`,`achievement`);

--
-- Indizes für die Tabelle `alarm`
--
ALTER TABLE `alarm`
  ADD PRIMARY KEY (`user_id`,`alarm_time`);

--
-- Indizes für die Tabelle `autorole`
--
ALTER TABLE `autorole`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `best_of_image`
--
ALTER TABLE `best_of_image`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `best_of_image_bl`
--
ALTER TABLE `best_of_image_bl`
  ADD PRIMARY KEY (`message_id`);

--
-- Indizes für die Tabelle `best_of_quote`
--
ALTER TABLE `best_of_quote`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `best_of_quote_bl`
--
ALTER TABLE `best_of_quote_bl`
  ADD PRIMARY KEY (`message_id`);

--
-- Indizes für die Tabelle `birthdays`
--
ALTER TABLE `birthdays`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indizes für die Tabelle `birthday_gratulation`
--
ALTER TABLE `birthday_gratulation`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indizes für die Tabelle `birthday_messages`
--
ALTER TABLE `birthday_messages`
  ADD PRIMARY KEY (`guild_id`);

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
-- Indizes für die Tabelle `giveawaylist`
--
ALTER TABLE `giveawaylist`
  ADD PRIMARY KEY (`guild_id`,`channel_id`,`message_id`);

--
-- Indizes für die Tabelle `guild`
--
ALTER TABLE `guild`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `image`
--
ALTER TABLE `image`
  ADD PRIMARY KEY (`image_name`);

--
-- Indizes für die Tabelle `interaction`
--
ALTER TABLE `interaction`
  ADD PRIMARY KEY (`interaction`);

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
-- Indizes für die Tabelle `leave_notifier`
--
ALTER TABLE `leave_notifier`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `level_role`
--
ALTER TABLE `level_role`
  ADD PRIMARY KEY (`guild_id`,`level`,`role_id`);

--
-- Indizes für die Tabelle `lobby`
--
ALTER TABLE `lobby`
  ADD PRIMARY KEY (`guild_id`,`channel_id`);

--
-- Indizes für die Tabelle `mediaonlychannel`
--
ALTER TABLE `mediaonlychannel`
  ADD PRIMARY KEY (`guild_id`,`channel_id`);

--
-- Indizes für die Tabelle `reaction_role`
--
ALTER TABLE `reaction_role`
  ADD PRIMARY KEY (`guild_id`,`channel_id`,`message_id`,`emoji`,`emote_guild_id`,`emote_id`);

--
-- Indizes für die Tabelle `reminder`
--
ALTER TABLE `reminder`
  ADD PRIMARY KEY (`user_id`,`reminder_time`);

--
-- Indizes für die Tabelle `streamers`
--
ALTER TABLE `streamers`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indizes für die Tabelle `streamer_mode`
--
ALTER TABLE `streamer_mode`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `streamhidden`
--
ALTER TABLE `streamhidden`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indizes für die Tabelle `streaming_role`
--
ALTER TABLE `streaming_role`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `stream_channel`
--
ALTER TABLE `stream_channel`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indizes für die Tabelle `toggle`
--
ALTER TABLE `toggle`
  ADD PRIMARY KEY (`guild_id`,`feature`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

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

--
-- Indizes für die Tabelle `user_votes`
--
ALTER TABLE `user_votes`
  ADD PRIMARY KEY (`message_id`,`user_id`);

--
-- Indizes für die Tabelle `votes`
--
ALTER TABLE `votes`
  ADD PRIMARY KEY (`message_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
