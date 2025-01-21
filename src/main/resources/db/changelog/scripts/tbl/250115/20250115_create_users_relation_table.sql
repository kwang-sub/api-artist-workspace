-- liquibase formatted sql
-- changeset kwang:20250115_create_user_relation_table.sql

create table if not exists tbl_contents
(
    id                 bigint primary key not null auto_increment comment '인덱스번호',
    contents_name      varchar(100)       not null comment '컨텐츠명',
    contents_og_name   varchar(255)       not null comment '컨텐츠원본명',
    contents_path      varchar(255)       not null comment '컨텐츠경로',
    contents_size      int                not null comment '컨텐츠사이즈',
    contents_type      varchar(100)       not null comment '컨텐츠유형',
    is_deleted         tinyint            not null comment '삭제여부',
    created_by         varchar(100)       not null comment '등록자id',
    created_date       datetime           not null comment '등록일시',
    last_modified_by   varchar(100)       not null comment '수정자id',
    last_modified_date datetime           not null comment '수정일시'
    );

alter table tbl_user
    add column logo_file_id bigint null comment '로고인덱스id' after role_id;
alter table tbl_user
    add column nickname varchar(255) null comment '닉네임' after user_name;
alter table tbl_user
    add column phone_number varchar(100) null comment '핸드폰번호' after email;
alter table tbl_user
    add constraint fk_user_content foreign key (logo_file_id) references tbl_contents (id);

update tbl_user set nickname = 'admin', phone_number = '01012341234' where login_id = 'admin';
alter table tbl_user modify column nickname varchar(255) not null comment '닉네임';
alter table tbl_user modify column phone_number varchar(100) not null comment '핸드폰번호';

create table if not exists tbl_user_sns
(
    id                 bigint primary key not null auto_increment comment '인덱스id',
    user_id           bigint             not null comment '사용자인덱스id',
    sns_type           varchar(255)       not null comment 'sns유형',
    sns_user_name      varchar(255)       not null comment 'sns계정',
    is_deleted         tinyint            not null comment '삭제여부',
    created_by         varchar(100)       not null comment '등록자id',
    created_date       datetime           not null comment '등록일시',
    last_modified_by   varchar(100)       not null comment '수정자id',
    last_modified_date datetime           not null comment '수정일시'
    );

alter table tbl_user_sns add constraint fk_user_sns_user foreign key (user_id) references tbl_user(id);