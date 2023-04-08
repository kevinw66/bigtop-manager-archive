--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--
-- DROP DATABASE IF EXISTS `ambari`;
-- DROP USER `ambari`;

# delimiter ;

# CREATE DATABASE `ambari` /*!40100 DEFAULT CHARACTER SET utf8 */;
#
# CREATE USER 'ambari' IDENTIFIED BY 'bigdata';

# USE @schema;

-- Set default_storage_engine to InnoDB
-- storage_engine variable should be used for versions prior to MySQL 5.6
set @version_short = substring_index(@@version, '.', 2);
set @major = cast(substring_index(@version_short, '.', 1) as SIGNED);
set @minor = cast(substring_index(@version_short, '.', -1) as SIGNED);
set @engine_stmt = IF((@major >= 5 AND @minor>=6) or @major >= 8, 'SET default_storage_engine=INNODB', 'SET storage_engine=INNODB');
prepare statement from @engine_stmt;
execute statement;
DEALLOCATE PREPARE statement;

CREATE TABLE `sequence` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `sequence_name` VARCHAR(100) NOT NULL,
    `next_val` BIGINT(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sequence_name` (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user` (
    `id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(32) DEFAULT NULL,
    `password` VARCHAR(32) DEFAULT NULL,
    `status` BIT(1) DEFAULT 1 COMMENT '0-disable, 1-enable',
    `create_time` DATETIME DEFAULT NULL,
    `update_time` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;