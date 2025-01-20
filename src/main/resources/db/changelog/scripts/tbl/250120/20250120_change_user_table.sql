-- liquibase formatted sql
-- changeset kwang:20250120_change_user_table.sql

alter table tbl_users_sns change column sns_user_name sns_username varchar(100) not null comment 'sns계정';
alter table tbl_users_sns modify column sns_type varchar(100) not null comment 'sns유형';
alter table tbl_users_sns change column users_id user_id bigint not null comment '사용자인덱스id';
