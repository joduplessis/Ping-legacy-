# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 46.101.59.60 (MySQL 5.5.46-0ubuntu0.14.04.2)
# Database: Ping
# Generation Time: 2017-03-15 07:48:41 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table categories
# ------------------------------------------------------------

DROP TABLE IF EXISTS `categories`;

CREATE TABLE `categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;

INSERT INTO `categories` (`id`, `user_id`, `title`, `created_at`, `updated_at`)
VALUES
	(1,1,'Admin','2016-01-08 09:24:05','2016-01-08 09:24:05'),
	(2,1,'Friends','2016-01-08 14:16:54','2016-01-08 14:16:54'),
	(3,1,'Family','2016-01-08 14:17:03','2016-01-08 14:17:03');

/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table migrations
# ------------------------------------------------------------

DROP TABLE IF EXISTS `migrations`;

CREATE TABLE `migrations` (
  `migration` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `migrations` WRITE;
/*!40000 ALTER TABLE `migrations` DISABLE KEYS */;

INSERT INTO `migrations` (`migration`, `batch`)
VALUES
	('2015_07_09_115103_create_pings_table',1),
	('2015_07_09_115108_create_users_table',1),
	('2015_07_09_115111_create_categories_table',1),
	('2015_07_09_115120_create_pingusers_table',1);

/*!40000 ALTER TABLE `migrations` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table pings
# ------------------------------------------------------------

DROP TABLE IF EXISTS `pings`;

CREATE TABLE `pings` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `days` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `category` int(11) NOT NULL,
  `repeat` tinyint(1) NOT NULL,
  `sound` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `fadein` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `pings` WRITE;
/*!40000 ALTER TABLE `pings` DISABLE KEYS */;

INSERT INTO `pings` (`id`, `user_id`, `title`, `time`, `days`, `category`, `repeat`, `sound`, `enabled`, `fadein`, `created_at`, `updated_at`)
VALUES
	(1,1,'Meeting about house','2015-01-01 16:11:00','Friday',1,1,'alarm_one',0,4,'2016-01-08 06:57:40','2016-01-08 14:11:13'),
	(2,1,'Time for gym','2015-01-01 05:00:00','Monday,Wednesday,Friday',1,1,'alarm_one',1,4,'2016-01-08 14:15:39','2016-01-08 14:15:39'),
	(3,1,'Lunch time','2015-01-01 13:00:00','Monday,Tuesday,Wednesday,Thursday,Friday',1,0,'alarm_one',0,0,'2016-01-08 14:16:20','2016-02-20 17:33:53');

/*!40000 ALTER TABLE `pings` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table pingusers
# ------------------------------------------------------------

DROP TABLE IF EXISTS `pingusers`;

CREATE TABLE `pingusers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ping_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `approved` tinyint(1) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



# Dump of table users
# ------------------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `social_id` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `status` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `badge` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` (`id`, `social_id`, `name`, `status`, `badge`, `token`, `created_at`, `updated_at`)
VALUES
	(1,'930417227020636','Johannes Benjamin Du Plessis','I really need to update my status','badge_mac','93041722702063628','2016-01-08 06:55:28','2016-01-26 16:33:37');

/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
