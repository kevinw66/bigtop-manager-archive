create database bigtop_manager default character set utf8;
create table bigtop_manager.user (
                                     id integer auto_increment primary key,
                                     create_time timestamp,
                                     update_time timestamp,
                                     nickname varchar(100),
                                     password varchar(100),
                                     status bool,
                                     username varchar(100)
);
INSERT INTO bigtop_manager.user (id, create_time, update_time, nickname, password, status, username)
VALUES (1, now(), now(), 'Administrator', '21232f297a57a5a743894a0e4a801fc3', true, 'admin');
