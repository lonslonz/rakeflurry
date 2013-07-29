SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `tb_server_runtime`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tb_server_runtime` ;

CREATE  TABLE IF NOT EXISTS `tb_server_runtime` (
  `server_runtime_id` INT NOT NULL AUTO_INCREMENT ,
  `server` VARCHAR(52) NULL ,
  `port` INT NULL ,
  `server_home` VARCHAR(512) NULL ,
  `version` VARCHAR(52) NULL ,
  `status` VARCHAR(32) NULL DEFAULT NULL ,
  `begin_time` DATETIME NULL DEFAULT NULL ,
  `end_time` DATETIME NULL DEFAULT NULL ,
  `update_time` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`server_runtime_id`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_server` ON `tb_server_runtime` (`server` ASC, `port` ASC, `begin_time` ASC) ;


-- -----------------------------------------------------
-- Table `tb_service_runtime`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tb_service_runtime` ;

CREATE  TABLE IF NOT EXISTS `tb_service_runtime` (
  `service_runtime_id` INT NOT NULL AUTO_INCREMENT ,
  `server_runtime_id` INT NOT NULL ,
  `url` VARCHAR(52) NULL ,
  `service_type` VARCHAR(32) NULL DEFAULT NULL ,
  `service_class` VARCHAR(512) NULL DEFAULT NULL ,
  `mbean_class` VARCHAR(512) NULL DEFAULT NULL ,
  `exec_type` VARCHAR(32) NULL DEFAULT NULL ,
  `status` VARCHAR(32) NULL DEFAULT NULL ,
  `reg_time` DATETIME NULL DEFAULT NULL ,
  `update_time` DATETIME NULL DEFAULT NULL ,
  PRIMARY KEY (`service_runtime_id`) )
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_server` ON `tb_service_runtime` (`server_runtime_id` ASC, `url` ASC) ;

CREATE INDEX `fk_tb_service_tb_server1` ON `tb_service_runtime` (`server_runtime_id` ASC) ;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
