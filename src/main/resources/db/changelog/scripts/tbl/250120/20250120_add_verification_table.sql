-- liquibase formatted sql
-- changeset kwang:20250120_add_verification_table.sql

create table if not exists tbl_user_verification (
     id bigint primary key not null auto_increment comment '인덱스번호',
     user_id bigint not null comment '역할인덱스id',
     verification_code varchar(100) not null comment '인증번호'
);

alter table tbl_user_verification add constraint fk_tbl_user_verification_user foreign key (user_id) references tbl_user(id);
