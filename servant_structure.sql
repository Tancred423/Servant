-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 03, 2019 at 11:52 AM
-- Server version: 10.1.41-MariaDB-0+deb9u1
-- PHP Version: 5.5.9-1ubuntu4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `servant_structure`
--

-- --------------------------------------------------------

--
-- Table structure for table `achievement`
--

CREATE TABLE IF NOT EXISTS `achievement` (
  `user_id` bigint(18) NOT NULL,
  `achievement` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ap` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`achievement`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `active_lobbies`
--

CREATE TABLE IF NOT EXISTS `active_lobbies` (
  `channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `alarm`
--

CREATE TABLE IF NOT EXISTS `alarm` (
  `user_id` bigint(18) NOT NULL,
  `alarm_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`,`alarm_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `autorole`
--

CREATE TABLE IF NOT EXISTS `autorole` (
  `guild_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `delay` int(11) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `baguette_counter`
--

CREATE TABLE IF NOT EXISTS `baguette_counter` (
  `user_id` bigint(18) NOT NULL,
  `baguette_size` int(11) NOT NULL,
  `size_counter` int(11) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_image`
--

CREATE TABLE IF NOT EXISTS `best_of_image` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `number` int(11) NOT NULL,
  `percentage` int(11) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_image_bl`
--

CREATE TABLE IF NOT EXISTS `best_of_image_bl` (
  `message_id` bigint(18) NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_quote`
--

CREATE TABLE IF NOT EXISTS `best_of_quote` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `number` int(11) NOT NULL,
  `percentage` int(11) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_quote_bl`
--

CREATE TABLE IF NOT EXISTS `best_of_quote_bl` (
  `message_id` bigint(18) NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bio`
--

CREATE TABLE IF NOT EXISTS `bio` (
  `user_id` bigint(18) NOT NULL,
  `text` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `birthdays`
--

CREATE TABLE IF NOT EXISTS `birthdays` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `birthday` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `birthday_gratulation`
--

CREATE TABLE IF NOT EXISTS `birthday_gratulation` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `was_gratulated` tinyint(1) NOT NULL,
  PRIMARY KEY (`guild_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `birthday_messages`
--

CREATE TABLE IF NOT EXISTS `birthday_messages` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `blacklist`
--

CREATE TABLE IF NOT EXISTS `blacklist` (
  `id` bigint(18) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `emote`
--

CREATE TABLE IF NOT EXISTS `emote` (
  `emote_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  PRIMARY KEY (`emote_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `feature_count`
--

CREATE TABLE IF NOT EXISTS `feature_count` (
  `id` bigint(18) NOT NULL,
  `feature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`id`,`feature`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `giveawaylist`
--

CREATE TABLE IF NOT EXISTS `giveawaylist` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `host_id` bigint(18) NOT NULL,
  `prize` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `amount_winners` int(11) NOT NULL,
  PRIMARY KEY (`guild_id`,`channel_id`,`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild`
--

CREATE TABLE IF NOT EXISTS `guild` (
  `guild_id` bigint(18) NOT NULL,
  `prefix` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `offset` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `birthday_channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `image`
--

CREATE TABLE IF NOT EXISTS `image` (
  `image_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`image_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `interaction`
--

CREATE TABLE IF NOT EXISTS `interaction` (
  `interaction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gif` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `interaction_count`
--

CREATE TABLE IF NOT EXISTS `interaction_count` (
  `user_id` bigint(18) NOT NULL,
  `interaction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `shared` int(11) NOT NULL,
  `received` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`interaction`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `join_notifier`
--

CREATE TABLE IF NOT EXISTS `join_notifier` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `leave_notifier`
--

CREATE TABLE IF NOT EXISTS `leave_notifier` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `level_role`
--

CREATE TABLE IF NOT EXISTS `level_role` (
  `guild_id` bigint(18) NOT NULL,
  `level` int(11) NOT NULL,
  `role_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`,`level`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lobby`
--

CREATE TABLE IF NOT EXISTS `lobby` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `mediaonlychannel`
--

CREATE TABLE IF NOT EXISTS `mediaonlychannel` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `reaction_role`
--

CREATE TABLE IF NOT EXISTS `reaction_role` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `emoji` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`,`channel_id`,`message_id`,`emoji`,`emote_guild_id`,`emote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reminder`
--

CREATE TABLE IF NOT EXISTS `reminder` (
  `user_id` bigint(18) NOT NULL,
  `reminder_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `topic` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`,`reminder_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `signup`
--

CREATE TABLE IF NOT EXISTS `signup` (
  `message_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `amount` int(11) NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `is_custom_date` tinyint(1) NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streamers`
--

CREATE TABLE IF NOT EXISTS `streamers` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streamer_mode`
--

CREATE TABLE IF NOT EXISTS `streamer_mode` (
  `guild_id` bigint(18) NOT NULL,
  `is_streamer_mode` tinyint(1) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streamhidden`
--

CREATE TABLE IF NOT EXISTS `streamhidden` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streaming_role`
--

CREATE TABLE IF NOT EXISTS `streaming_role` (
  `guild_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stream_channel`
--

CREATE TABLE IF NOT EXISTS `stream_channel` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  PRIMARY KEY (`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `toggle`
--

CREATE TABLE IF NOT EXISTS `toggle` (
  `guild_id` bigint(18) NOT NULL,
  `feature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`guild_id`,`feature`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `user_id` bigint(18) NOT NULL,
  `offset` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prefix` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_exp`
--

CREATE TABLE IF NOT EXISTS `user_exp` (
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `exp` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`guild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_settings`
--

CREATE TABLE IF NOT EXISTS `user_settings` (
  `user_id` bigint(18) NOT NULL,
  `setting` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `value` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`user_id`,`setting`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_votes`
--

CREATE TABLE IF NOT EXISTS `user_votes` (
  `message_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`message_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `votes`
--

CREATE TABLE IF NOT EXISTS `votes` (
  `message_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
