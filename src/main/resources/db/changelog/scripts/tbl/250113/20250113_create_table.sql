-- liquibase formatted sql
-- changeset kwang:20250113_create_table.sql

create table if not exists tbl_user
(
    id                   bigint       not null primary key auto_increment comment '인덱스번호',
    role_id              BIGINT       not null comment '역할인덱스ID',
    login_id             VARCHAR(100) not null comment '로그인아이디',
    password             VARCHAR(100) not null comment '비밀번호',
    user_name            VARCHAR(255) not null comment '성명',
    email                VARCHAR(100) not null comment '이메일',
    is_activated         TINYINT      not null comment '활성화여부',
    is_use_temp_password TINYINT      not null comment '임시비밀번호사용여부',
    is_deleted           TINYINT      not null comment '삭제여부',
    created_by           VARCHAR(100) not null comment '등록자ID',
    created_date         DATETIME     not null comment '등록일시',
    last_modified_by     VARCHAR(100) not null comment '수정자ID',
    last_modified_date   DATETIME     not null comment '수정일시'
);
insert into tbl_user (id, role_id, login_id, password, user_name, email, is_activated, is_use_temp_password,
                       is_deleted, created_by, created_date, last_modified_by, last_modified_date)
    values (1, 1, 'admin', '$2a$10$ErVGsvBtS2QRprCiNGmxKuIfss/l7bfT.qp4xHZ/35P1hj.NtuXxa', '어드민',
           'choikwangsub@gmail.com', 1, 0, 0, 'admin', now(), 'admin', now());


create table if not exists tbl_role
(
    id                 bigint       not null primary key auto_increment comment '인덱스id',
    role_code          CHAR(2)      null comment '역할코드',
    role_name          VARCHAR(100) not null comment '역할명',
    role_desc          VARCHAR(100) null comment '역할설명',
    is_deleted         TINYINT      not null comment '삭제여부',
    created_by         VARCHAR(100) not null comment '등록자ID',
    created_date       DATETIME     not null comment '등록일시',
    last_modified_by   VARCHAR(100) not null comment '수정자ID',
    last_modified_date DATETIME     not null comment '수정일시'
);


insert into tbl_role(id, role_code, role_name, role_desc, is_deleted, created_by, created_date, last_modified_by,
                     last_modified_date)
values (1, '00', 'ROLE_ADMIN', 'admin', 0, 'admin', now(), 'admin', now()),
       (2, '01', 'ROLE_ARTIST', 'artist', 0, 'admin', now(), 'admin', now());

alter table tbl_user
    add constraint fk_tbl_user_role foreign key (role_id) references tbl_role (id);