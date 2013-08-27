/*
SQLyog Community v10.1 Beta1
MySQL - 5.5.21 : Database - rakeflurry
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
USE `rakeflurry`;

/*Table structure for table `tb_accesscode` */

CREATE TABLE `tb_accesscode` (
  `accesscode_id` int(11) NOT NULL AUTO_INCREMENT,
  `dashboard_id` int(11) NOT NULL,
  `access_code` varchar(256) NOT NULL,
  `mbr_no` varchar(256) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `total_count` int(11) DEFAULT NULL,
  `running_status` varchar(32) DEFAULT NULL,
  `source_uri` varchar(1024) DEFAULT NULL,
  `worker` varchar(64) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`accesscode_id`)
) ENGINE=InnoDB AUTO_INCREMENT=230 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_api` */

CREATE TABLE `tb_api` (
  `api_id` int(11) NOT NULL AUTO_INCREMENT,
  `apikey_id` int(11) NOT NULL,
  `api` varchar(256) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `elapsed` int(11) DEFAULT NULL,
  `running_status` varchar(32) DEFAULT NULL,
  `uri` varchar(1024) DEFAULT NULL,
  `req_url` varchar(1024) DEFAULT NULL,
  `retry_count` int(11) DEFAULT '0',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3980 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_apikey` */

CREATE TABLE `tb_apikey` (
  `apikey_id` int(11) NOT NULL AUTO_INCREMENT,
  `accesscode_id` int(11) NOT NULL,
  `apikey` varchar(256) NOT NULL,
  `name` varchar(256) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `total_count` int(11) DEFAULT NULL,
  `running_status` varchar(32) DEFAULT NULL,
  `filename` varchar(1024) DEFAULT NULL,
  `error_msg` varchar(2048) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`apikey_id`)
) ENGINE=InnoDB AUTO_INCREMENT=461 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_appmetrics` */

CREATE TABLE `tb_appmetrics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `metric_name` varchar(256) NOT NULL,
  `used` int(11) DEFAULT '1',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_dashboard` */

CREATE TABLE `tb_dashboard` (
  `dashboard_id` int(11) NOT NULL AUTO_INCREMENT,
  `total_count` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `call_start_day` varchar(32) DEFAULT NULL,
  `call_end_day` varchar(32) DEFAULT NULL,
  `recover_who_id` int(11) DEFAULT NULL,
  `recover_me_id` int(11) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`dashboard_id`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_keymap` */

CREATE TABLE `tb_keymap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mbr_no` varchar(128) NOT NULL,
  `access_code` varchar(128) NOT NULL,
  `api_key` varchar(128) NOT NULL,
  `api_key_name` varchar(256) DEFAULT NULL,
  `used` int(11) DEFAULT '1',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `mbr_no` (`mbr_no`,`api_key`)
) ENGINE=InnoDB AUTO_INCREMENT=119 DEFAULT CHARSET=utf8;

/*Table structure for table `tb_rcpt` */

CREATE TABLE `tb_rcpt` (
  `rcpt_id` int(11) NOT NULL AUTO_INCREMENT,
  `rcpt_addr` varchar(128) DEFAULT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rcpt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_worker` */

CREATE TABLE `tb_worker` (
  `worker_id` int(11) NOT NULL AUTO_INCREMENT,
  `server_addr` varchar(64) NOT NULL,
  `worker_count` int(11) DEFAULT '1',
  `valid` int(11) DEFAULT '1',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`worker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (1,'ActiveUsers',1,'2013-08-07 16:31:44');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (2,'ActiveUsersByWeek',1,'2013-08-07 11:21:07');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (3,'ActiveUsersByMonth',1,'2013-08-07 11:21:09');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (4,'NewUsers',1,'2013-08-07 11:21:10');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (5,'MedianSessionLength',1,'2013-08-07 11:21:13');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (6,'AvgSessionLength',1,'2013-08-07 11:21:16');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (7,'Sessions',1,'2013-08-07 11:21:19');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (8,'RetainedUsers',1,'2013-08-07 11:21:21');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (9,'PageViews',1,'2013-08-07 11:21:23');
insert  into `tb_appmetrics`(`id`,`metric_name`,`used`,`update_time`) values (10,'AvgPageViewsPerSession',1,'2013-08-07 11:21:23');


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
