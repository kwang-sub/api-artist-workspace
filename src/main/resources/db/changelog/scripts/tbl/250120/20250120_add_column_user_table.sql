-- liquibase formatted sql
-- changeset kwang:20250120_add_column_user_table.sql

alter table tbl_user add column bio VARCHAR(100) null comment '자기소개' after phone_number;
alter table tbl_user add column workspace_name VARCHAR(100) default '' not null comment '작업공간명' after nickname;
