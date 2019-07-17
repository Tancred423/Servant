-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jul 17, 2019 at 02:06 PM
-- Server version: 5.6.34-log
-- PHP Version: 7.2.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
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
-- Table structure for table `autorole`
--

CREATE TABLE `autorole` (
  `guild_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `emote`
--

CREATE TABLE `emote` (
  `emote_name` varchar(32) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `feature_count`
--

CREATE TABLE `feature_count` (
  `id` bigint(18) NOT NULL,
  `feature` varchar(32) NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `guild_settings`
--

CREATE TABLE `guild_settings` (
  `guild_id` bigint(18) NOT NULL,
  `setting` varchar(32) NOT NULL,
  `value` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `interaction`
--

CREATE TABLE `interaction` (
  `interaction` varchar(32) NOT NULL,
  `gif` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `interaction_count`
--

CREATE TABLE `interaction_count` (
  `user_id` bigint(18) NOT NULL,
  `interaction` varchar(32) NOT NULL,
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
-- Table structure for table `toggle`
--

CREATE TABLE `toggle` (
  `guild_id` bigint(18) NOT NULL,
  `feature` varchar(32) NOT NULL,
  `is_enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `setting` varchar(32) NOT NULL,
  `value` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `autorole`
--
ALTER TABLE `autorole`
  ADD PRIMARY KEY (`guild_id`,`role_id`);

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
-- Indexes for table `guild_settings`
--
ALTER TABLE `guild_settings`
  ADD PRIMARY KEY (`guild_id`,`setting`);

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
-- Indexes for table `toggle`
--
ALTER TABLE `toggle`
  ADD PRIMARY KEY (`guild_id`,`feature`);

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
