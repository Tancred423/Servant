-- phpMyAdmin SQL Dump
-- version 4.6.6deb4
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 31, 2019 at 10:56 PM
-- Server version: 10.1.41-MariaDB-0+deb9u1
-- PHP Version: 7.0.33-0+deb9u5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `servant_structure`
--

-- --------------------------------------------------------

--
-- Table structure for table `achievement`
--

CREATE TABLE `achievement` (
  `user_id` bigint(18) NOT NULL,
  `achievement` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ap` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `active_lobbies`
--

CREATE TABLE `active_lobbies` (
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `alarm`
--

CREATE TABLE `alarm` (
  `user_id` bigint(18) NOT NULL,
  `alarm_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `autorole`
--

CREATE TABLE `autorole` (
  `guild_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `delay` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `baguette_counter`
--

CREATE TABLE `baguette_counter` (
  `user_id` bigint(18) NOT NULL,
  `baguette_size` int(11) NOT NULL,
  `size_counter` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_image`
--

CREATE TABLE `best_of_image` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `number` int(11) NOT NULL,
  `percentage` int(11) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_image_bl`
--

CREATE TABLE `best_of_image_bl` (
  `message_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_quote`
--

CREATE TABLE `best_of_quote` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `number` int(11) NOT NULL,
  `percentage` int(11) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `best_of_quote_bl`
--

CREATE TABLE `best_of_quote_bl` (
  `message_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bio`
--

CREATE TABLE `bio` (
  `user_id` bigint(18) NOT NULL,
  `text` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `birthdays`
--

CREATE TABLE `birthdays` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `birthday` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `birthday_gratulation`
--

CREATE TABLE `birthday_gratulation` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `was_gratulated` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `birthday_messages`
--

CREATE TABLE `birthday_messages` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `blacklist`
--

CREATE TABLE `blacklist` (
  `id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `emote`
--

CREATE TABLE `emote` (
  `emote_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `feature_count`
--

CREATE TABLE `feature_count` (
  `id` bigint(18) NOT NULL,
  `feature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `giveawaylist`
--

CREATE TABLE `giveawaylist` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL,
  `message_id` bigint(18) NOT NULL,
  `host_id` bigint(18) NOT NULL,
  `prize` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `amount_winners` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild`
--

CREATE TABLE `guild` (
  `guild_id` bigint(18) NOT NULL,
  `prefix` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `offset` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `birthday_channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `image`
--

CREATE TABLE `image` (
  `image_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `interaction`
--

CREATE TABLE `interaction` (
  `interaction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gif` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `interaction_count`
--

CREATE TABLE `interaction_count` (
  `user_id` bigint(18) NOT NULL,
  `interaction` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `shared` int(11) NOT NULL,
  `received` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `join_notifier`
--

CREATE TABLE `join_notifier` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `leave_notifier`
--

CREATE TABLE `leave_notifier` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `level_role`
--

CREATE TABLE `level_role` (
  `guild_id` bigint(18) NOT NULL,
  `level` int(11) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lobby`
--

CREATE TABLE `lobby` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `mediaonlychannel`
--

CREATE TABLE `mediaonlychannel` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `reaction_role`
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
-- Table structure for table `reminder`
--

CREATE TABLE `reminder` (
  `user_id` bigint(18) NOT NULL,
  `reminder_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `topic` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `signup`
--

CREATE TABLE `signup` (
  `message_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `amount` int(11) NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streamers`
--

CREATE TABLE `streamers` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streamer_mode`
--

CREATE TABLE `streamer_mode` (
  `guild_id` bigint(18) NOT NULL,
  `is_streamer_mode` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streamhidden`
--

CREATE TABLE `streamhidden` (
  `guild_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `streaming_role`
--

CREATE TABLE `streaming_role` (
  `guild_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stream_channel`
--

CREATE TABLE `stream_channel` (
  `guild_id` bigint(18) NOT NULL,
  `channel_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `toggle`
--

CREATE TABLE `toggle` (
  `guild_id` bigint(18) NOT NULL,
  `feature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` bigint(18) NOT NULL,
  `offset` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `prefix` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_exp`
--

CREATE TABLE `user_exp` (
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `exp` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_settings`
--

CREATE TABLE `user_settings` (
  `user_id` bigint(18) NOT NULL,
  `setting` varchar(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `value` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_votes`
--

CREATE TABLE `user_votes` (
  `message_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL,
  `emoji` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `votes`
--

CREATE TABLE `votes` (
  `message_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `achievement`
--
ALTER TABLE `achievement`
  ADD PRIMARY KEY (`user_id`,`achievement`);

--
-- Indexes for table `active_lobbies`
--
ALTER TABLE `active_lobbies`
  ADD PRIMARY KEY (`channel_id`);

--
-- Indexes for table `alarm`
--
ALTER TABLE `alarm`
  ADD PRIMARY KEY (`user_id`,`alarm_time`);

--
-- Indexes for table `autorole`
--
ALTER TABLE `autorole`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `baguette_counter`
--
ALTER TABLE `baguette_counter`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `best_of_image`
--
ALTER TABLE `best_of_image`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `best_of_image_bl`
--
ALTER TABLE `best_of_image_bl`
  ADD PRIMARY KEY (`message_id`);

--
-- Indexes for table `best_of_quote`
--
ALTER TABLE `best_of_quote`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `best_of_quote_bl`
--
ALTER TABLE `best_of_quote_bl`
  ADD PRIMARY KEY (`message_id`);

--
-- Indexes for table `bio`
--
ALTER TABLE `bio`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `birthday_gratulation`
--
ALTER TABLE `birthday_gratulation`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indexes for table `birthday_messages`
--
ALTER TABLE `birthday_messages`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `blacklist`
--
ALTER TABLE `blacklist`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `emote`
--
ALTER TABLE `emote`
  ADD PRIMARY KEY (`emote_name`);

--
-- Indexes for table `feature_count`
--
ALTER TABLE `feature_count`
  ADD PRIMARY KEY (`id`,`feature`);

--
-- Indexes for table `giveawaylist`
--
ALTER TABLE `giveawaylist`
  ADD PRIMARY KEY (`guild_id`,`channel_id`,`message_id`);

--
-- Indexes for table `guild`
--
ALTER TABLE `guild`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `image`
--
ALTER TABLE `image`
  ADD PRIMARY KEY (`image_name`);

--
-- Indexes for table `interaction_count`
--
ALTER TABLE `interaction_count`
  ADD PRIMARY KEY (`user_id`,`interaction`);

--
-- Indexes for table `join_notifier`
--
ALTER TABLE `join_notifier`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `leave_notifier`
--
ALTER TABLE `leave_notifier`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `level_role`
--
ALTER TABLE `level_role`
  ADD PRIMARY KEY (`guild_id`,`level`,`role_id`);

--
-- Indexes for table `lobby`
--
ALTER TABLE `lobby`
  ADD PRIMARY KEY (`guild_id`,`channel_id`);

--
-- Indexes for table `mediaonlychannel`
--
ALTER TABLE `mediaonlychannel`
  ADD PRIMARY KEY (`guild_id`,`channel_id`);

--
-- Indexes for table `reaction_role`
--
ALTER TABLE `reaction_role`
  ADD PRIMARY KEY (`guild_id`,`channel_id`,`message_id`,`emoji`,`emote_guild_id`,`emote_id`);

--
-- Indexes for table `reminder`
--
ALTER TABLE `reminder`
  ADD PRIMARY KEY (`user_id`,`reminder_time`);

--
-- Indexes for table `signup`
--
ALTER TABLE `signup`
  ADD PRIMARY KEY (`message_id`);

--
-- Indexes for table `streamers`
--
ALTER TABLE `streamers`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indexes for table `streamer_mode`
--
ALTER TABLE `streamer_mode`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `streamhidden`
--
ALTER TABLE `streamhidden`
  ADD PRIMARY KEY (`guild_id`,`user_id`);

--
-- Indexes for table `streaming_role`
--
ALTER TABLE `streaming_role`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `stream_channel`
--
ALTER TABLE `stream_channel`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `toggle`
--
ALTER TABLE `toggle`
  ADD PRIMARY KEY (`guild_id`,`feature`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `user_exp`
--
ALTER TABLE `user_exp`
  ADD PRIMARY KEY (`user_id`,`guild_id`);

--
-- Indexes for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD PRIMARY KEY (`user_id`,`setting`);

--
-- Indexes for table `user_votes`
--
ALTER TABLE `user_votes`
  ADD PRIMARY KEY (`message_id`,`user_id`);

--
-- Indexes for table `votes`
--
ALTER TABLE `votes`
  ADD PRIMARY KEY (`message_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
