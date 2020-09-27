-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 27, 2020 at 09:52 PM
-- Server version: 10.1.41-MariaDB-0+deb9u1
-- PHP Version: 5.5.9-1ubuntu4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `servant_production`
--

-- --------------------------------------------------------

--
-- Table structure for table `const_achievements`
--

CREATE TABLE `const_achievements` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ap` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_categories`
--

CREATE TABLE `const_categories` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_mod` tinyint(1) NOT NULL,
  `is_toggleable` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_commands`
--

CREATE TABLE `const_commands` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_emojis`
--

CREATE TABLE `const_emojis` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emoji` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_emotes`
--

CREATE TABLE `const_emotes` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `emote_guild_id` bigint(18) NOT NULL,
  `emote_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_features`
--

CREATE TABLE `const_features` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enabled_by_default` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_images`
--

CREATE TABLE `const_images` (
  `id` int(11) NOT NULL,
  `image_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_interactions`
--

CREATE TABLE `const_interactions` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_languages`
--

CREATE TABLE `const_languages` (
  `code` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_plugins`
--

CREATE TABLE `const_plugins` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enabled_by_default` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_poll_types`
--

CREATE TABLE `const_poll_types` (
  `id` int(11) NOT NULL,
  `poll_type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_profile_images`
--

CREATE TABLE `const_profile_images` (
  `id` int(11) NOT NULL,
  `image_url` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `const_timezones`
--

CREATE TABLE `const_timezones` (
  `id` int(11) NOT NULL,
  `timezone` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `custom_commands`
--

CREATE TABLE `custom_commands` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `invoke` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `normal_msg` text COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `custom_commands_aliases`
--

CREATE TABLE `custom_commands_aliases` (
  `id` int(11) NOT NULL,
  `cc_id` int(11) NOT NULL,
  `alias` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `custom_commands_embeds`
--

CREATE TABLE `custom_commands_embeds` (
  `id` int(11) NOT NULL,
  `cc_id` int(11) NOT NULL,
  `colorcode` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_name` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_icon_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `thumbnail_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `footer` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL,
  `footer_icon_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `custom_commands_fields`
--

CREATE TABLE `custom_commands_fields` (
  `cc_id` int(11) NOT NULL,
  `field_no` int(11) NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL,
  `inline` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `giveaways`
--

CREATE TABLE `giveaways` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `prize` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `amount_winners` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `global_blacklist`
--

CREATE TABLE `global_blacklist` (
  `user_or_guild_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guilds`
--

CREATE TABLE `guilds` (
  `guild_id` bigint(18) NOT NULL,
  `prefix` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language_code` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `timezone_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_autoroles`
--

CREATE TABLE `guild_autoroles` (
  `guild_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL,
  `delay` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_best_of_images`
--

CREATE TABLE `guild_best_of_images` (
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `min_votes_flat` int(11) NOT NULL,
  `min_votes_percent` int(11) NOT NULL,
  `emoji` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_best_of_quotes`
--

CREATE TABLE `guild_best_of_quotes` (
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `min_votes_flat` int(11) NOT NULL,
  `min_votes_percent` int(11) NOT NULL,
  `emoji` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_birthdays`
--

CREATE TABLE `guild_birthdays` (
  `guild_id` bigint(18) NOT NULL,
  `list_tc_id` bigint(18) NOT NULL,
  `list_msg_id` bigint(18) NOT NULL,
  `list_author_id` bigint(18) NOT NULL,
  `servant_bday` tinyint(1) NOT NULL,
  `announcement_tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_command_counts`
--

CREATE TABLE `guild_command_counts` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `command_id` int(11) NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_disabled_categories`
--

CREATE TABLE `guild_disabled_categories` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `category_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_disabled_commands`
--

CREATE TABLE `guild_disabled_commands` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `command_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_disabled_features`
--

CREATE TABLE `guild_disabled_features` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `feature_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_disabled_plugins`
--

CREATE TABLE `guild_disabled_plugins` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `plugin_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_joins`
--

CREATE TABLE `guild_joins` (
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_leaves`
--

CREATE TABLE `guild_leaves` (
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_level`
--

CREATE TABLE `guild_level` (
  `guild_id` bigint(18) NOT NULL,
  `modifier` float NOT NULL,
  `notification` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_level_roles`
--

CREATE TABLE `guild_level_roles` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `level` int(3) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_livestreamers`
--

CREATE TABLE `guild_livestreamers` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_livestreams`
--

CREATE TABLE `guild_livestreams` (
  `guild_id` bigint(18) NOT NULL,
  `is_public` tinyint(1) NOT NULL,
  `role_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `ping_role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_logs`
--

CREATE TABLE `guild_logs` (
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_update` tinyint(1) NOT NULL,
  `msg_delete` tinyint(1) NOT NULL,
  `category_create` tinyint(1) NOT NULL,
  `category_delete` tinyint(1) NOT NULL,
  `tc_create` tinyint(1) NOT NULL,
  `tc_delete` tinyint(1) NOT NULL,
  `vc_create` tinyint(1) NOT NULL,
  `vc_delete` tinyint(1) NOT NULL,
  `vc_join` tinyint(1) NOT NULL,
  `vc_move` tinyint(1) NOT NULL,
  `vc_leave` tinyint(1) NOT NULL,
  `user_ban` tinyint(1) NOT NULL,
  `user_unban` tinyint(1) NOT NULL,
  `invite_create` tinyint(1) NOT NULL,
  `invite_delete` tinyint(1) NOT NULL,
  `user_join` tinyint(1) NOT NULL,
  `user_leave` tinyint(1) NOT NULL,
  `role_add` tinyint(1) NOT NULL,
  `role_remove` tinyint(1) NOT NULL,
  `role_create` tinyint(1) NOT NULL,
  `role_delete` tinyint(1) NOT NULL,
  `emote_add` tinyint(1) NOT NULL,
  `emote_remove` tinyint(1) NOT NULL,
  `boost_count` tinyint(1) NOT NULL,
  `boost_tier` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_media_only_channels`
--

CREATE TABLE `guild_media_only_channels` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_mod_roles`
--

CREATE TABLE `guild_mod_roles` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `role_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `guild_voice_lobbies`
--

CREATE TABLE `guild_voice_lobbies` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `vc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `polls`
--

CREATE TABLE `polls` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `poll_type_id` int(1) NOT NULL,
  `ending_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `amount_answers` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ratings`
--

CREATE TABLE `ratings` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `event_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `topic` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reaction_roles`
--

CREATE TABLE `reaction_roles` (
  `id` int(11) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `emoji` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reaction_role_fields`
--

CREATE TABLE `reaction_role_fields` (
  `msg_id` bigint(18) NOT NULL,
  `field_no` int(11) NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL,
  `inline` tinyint(1) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reaction_role_messages`
--

CREATE TABLE `reaction_role_messages` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `colorcode` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_name` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `author_icon_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `thumbnail_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `footer` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL,
  `footer_icon_url` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `remind_mes`
--

CREATE TABLE `remind_mes` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `event_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `topic` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `signups`
--

CREATE TABLE `signups` (
  `id` int(11) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `author_id` bigint(18) NOT NULL,
  `amount_participants` int(11) NOT NULL,
  `title` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_best_of_image_bl`
--

CREATE TABLE `tmp_best_of_image_bl` (
  `msg_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_best_of_quote_bl`
--

CREATE TABLE `tmp_best_of_quote_bl` (
  `msg_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_birthday_gratulated`
--

CREATE TABLE `tmp_birthday_gratulated` (
  `id` int(11) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_giveaway_participants`
--

CREATE TABLE `tmp_giveaway_participants` (
  `id` int(11) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_poll_participants`
--

CREATE TABLE `tmp_poll_participants` (
  `id` int(11) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `reaction` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_rating_participants`
--

CREATE TABLE `tmp_rating_participants` (
  `id` int(11) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `reaction` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_remindme_participants`
--

CREATE TABLE `tmp_remindme_participants` (
  `id` int(11) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_signup_participants`
--

CREATE TABLE `tmp_signup_participants` (
  `id` int(11) NOT NULL,
  `msg_id` bigint(18) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `tc_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tmp_voice_lobbies_active`
--

CREATE TABLE `tmp_voice_lobbies_active` (
  `vc_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` bigint(18) NOT NULL,
  `prefix` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `language_code` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color_code` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bio` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `birthday` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `profile_bg_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_achievements`
--

CREATE TABLE `user_achievements` (
  `id` int(11) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `achievement_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_baguettes`
--

CREATE TABLE `user_baguettes` (
  `user_id` bigint(18) NOT NULL,
  `size` int(11) NOT NULL,
  `counter` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_birthday_guilds`
--

CREATE TABLE `user_birthday_guilds` (
  `id` int(11) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_command_counts`
--

CREATE TABLE `user_command_counts` (
  `id` int(11) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `command_id` int(11) NOT NULL,
  `count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_exp`
--

CREATE TABLE `user_exp` (
  `id` int(11) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `guild_id` bigint(18) NOT NULL,
  `exp` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_interaction_counts`
--

CREATE TABLE `user_interaction_counts` (
  `id` int(11) NOT NULL,
  `user_id` bigint(18) NOT NULL,
  `interaction_id` int(11) NOT NULL,
  `shared` int(11) NOT NULL,
  `received` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_ttt_statistics`
--

CREATE TABLE `user_ttt_statistics` (
  `user_id` bigint(18) NOT NULL,
  `wins` int(11) NOT NULL,
  `draws` int(11) NOT NULL,
  `loses` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `const_achievements`
--
ALTER TABLE `const_achievements`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_categories`
--
ALTER TABLE `const_categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_commands`
--
ALTER TABLE `const_commands`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_emojis`
--
ALTER TABLE `const_emojis`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_emotes`
--
ALTER TABLE `const_emotes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_features`
--
ALTER TABLE `const_features`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_images`
--
ALTER TABLE `const_images`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_interactions`
--
ALTER TABLE `const_interactions`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_languages`
--
ALTER TABLE `const_languages`
  ADD PRIMARY KEY (`code`);

--
-- Indexes for table `const_plugins`
--
ALTER TABLE `const_plugins`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_poll_types`
--
ALTER TABLE `const_poll_types`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_profile_images`
--
ALTER TABLE `const_profile_images`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `const_timezones`
--
ALTER TABLE `const_timezones`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `custom_commands`
--
ALTER TABLE `custom_commands`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `custom_commands_aliases`
--
ALTER TABLE `custom_commands_aliases`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `custom_commands_embeds`
--
ALTER TABLE `custom_commands_embeds`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `custom_commands_fields`
--
ALTER TABLE `custom_commands_fields`
  ADD PRIMARY KEY (`cc_id`,`field_no`);

--
-- Indexes for table `giveaways`
--
ALTER TABLE `giveaways`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `global_blacklist`
--
ALTER TABLE `global_blacklist`
  ADD PRIMARY KEY (`user_or_guild_id`);

--
-- Indexes for table `guilds`
--
ALTER TABLE `guilds`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_autoroles`
--
ALTER TABLE `guild_autoroles`
  ADD PRIMARY KEY (`guild_id`,`role_id`);

--
-- Indexes for table `guild_best_of_images`
--
ALTER TABLE `guild_best_of_images`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_best_of_quotes`
--
ALTER TABLE `guild_best_of_quotes`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_birthdays`
--
ALTER TABLE `guild_birthdays`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_command_counts`
--
ALTER TABLE `guild_command_counts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_disabled_categories`
--
ALTER TABLE `guild_disabled_categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_disabled_commands`
--
ALTER TABLE `guild_disabled_commands`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_disabled_features`
--
ALTER TABLE `guild_disabled_features`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_disabled_plugins`
--
ALTER TABLE `guild_disabled_plugins`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_joins`
--
ALTER TABLE `guild_joins`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_leaves`
--
ALTER TABLE `guild_leaves`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_level`
--
ALTER TABLE `guild_level`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_level_roles`
--
ALTER TABLE `guild_level_roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_livestreamers`
--
ALTER TABLE `guild_livestreamers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_livestreams`
--
ALTER TABLE `guild_livestreams`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_logs`
--
ALTER TABLE `guild_logs`
  ADD PRIMARY KEY (`guild_id`);

--
-- Indexes for table `guild_media_only_channels`
--
ALTER TABLE `guild_media_only_channels`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_mod_roles`
--
ALTER TABLE `guild_mod_roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `guild_voice_lobbies`
--
ALTER TABLE `guild_voice_lobbies`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `polls`
--
ALTER TABLE `polls`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reaction_roles`
--
ALTER TABLE `reaction_roles`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `reaction_role_fields`
--
ALTER TABLE `reaction_role_fields`
  ADD PRIMARY KEY (`msg_id`,`field_no`);

--
-- Indexes for table `reaction_role_messages`
--
ALTER TABLE `reaction_role_messages`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `remind_mes`
--
ALTER TABLE `remind_mes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `signups`
--
ALTER TABLE `signups`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_best_of_image_bl`
--
ALTER TABLE `tmp_best_of_image_bl`
  ADD PRIMARY KEY (`msg_id`);

--
-- Indexes for table `tmp_best_of_quote_bl`
--
ALTER TABLE `tmp_best_of_quote_bl`
  ADD PRIMARY KEY (`msg_id`);

--
-- Indexes for table `tmp_birthday_gratulated`
--
ALTER TABLE `tmp_birthday_gratulated`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_giveaway_participants`
--
ALTER TABLE `tmp_giveaway_participants`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_poll_participants`
--
ALTER TABLE `tmp_poll_participants`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_rating_participants`
--
ALTER TABLE `tmp_rating_participants`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_remindme_participants`
--
ALTER TABLE `tmp_remindme_participants`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_signup_participants`
--
ALTER TABLE `tmp_signup_participants`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tmp_voice_lobbies_active`
--
ALTER TABLE `tmp_voice_lobbies_active`
  ADD PRIMARY KEY (`vc_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `user_achievements`
--
ALTER TABLE `user_achievements`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_baguettes`
--
ALTER TABLE `user_baguettes`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `user_birthday_guilds`
--
ALTER TABLE `user_birthday_guilds`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_command_counts`
--
ALTER TABLE `user_command_counts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_exp`
--
ALTER TABLE `user_exp`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_interaction_counts`
--
ALTER TABLE `user_interaction_counts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `user_ttt_statistics`
--
ALTER TABLE `user_ttt_statistics`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `const_achievements`
--
ALTER TABLE `const_achievements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_categories`
--
ALTER TABLE `const_categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_commands`
--
ALTER TABLE `const_commands`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_emojis`
--
ALTER TABLE `const_emojis`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_emotes`
--
ALTER TABLE `const_emotes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_features`
--
ALTER TABLE `const_features`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_images`
--
ALTER TABLE `const_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_interactions`
--
ALTER TABLE `const_interactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_plugins`
--
ALTER TABLE `const_plugins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_poll_types`
--
ALTER TABLE `const_poll_types`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_profile_images`
--
ALTER TABLE `const_profile_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `const_timezones`
--
ALTER TABLE `const_timezones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `custom_commands`
--
ALTER TABLE `custom_commands`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `custom_commands_aliases`
--
ALTER TABLE `custom_commands_aliases`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `custom_commands_embeds`
--
ALTER TABLE `custom_commands_embeds`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `giveaways`
--
ALTER TABLE `giveaways`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_best_of_quotes`
--
ALTER TABLE `guild_best_of_quotes`
  MODIFY `guild_id` bigint(18) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_command_counts`
--
ALTER TABLE `guild_command_counts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_disabled_categories`
--
ALTER TABLE `guild_disabled_categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_disabled_commands`
--
ALTER TABLE `guild_disabled_commands`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_disabled_features`
--
ALTER TABLE `guild_disabled_features`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_disabled_plugins`
--
ALTER TABLE `guild_disabled_plugins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_level_roles`
--
ALTER TABLE `guild_level_roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_livestreamers`
--
ALTER TABLE `guild_livestreamers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_media_only_channels`
--
ALTER TABLE `guild_media_only_channels`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_mod_roles`
--
ALTER TABLE `guild_mod_roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `guild_voice_lobbies`
--
ALTER TABLE `guild_voice_lobbies`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `polls`
--
ALTER TABLE `polls`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reaction_roles`
--
ALTER TABLE `reaction_roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reaction_role_messages`
--
ALTER TABLE `reaction_role_messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `remind_mes`
--
ALTER TABLE `remind_mes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `signups`
--
ALTER TABLE `signups`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tmp_birthday_gratulated`
--
ALTER TABLE `tmp_birthday_gratulated`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tmp_giveaway_participants`
--
ALTER TABLE `tmp_giveaway_participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tmp_poll_participants`
--
ALTER TABLE `tmp_poll_participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tmp_rating_participants`
--
ALTER TABLE `tmp_rating_participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tmp_remindme_participants`
--
ALTER TABLE `tmp_remindme_participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tmp_signup_participants`
--
ALTER TABLE `tmp_signup_participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` bigint(18) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_achievements`
--
ALTER TABLE `user_achievements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_birthday_guilds`
--
ALTER TABLE `user_birthday_guilds`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_command_counts`
--
ALTER TABLE `user_command_counts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_exp`
--
ALTER TABLE `user_exp`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_interaction_counts`
--
ALTER TABLE `user_interaction_counts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
