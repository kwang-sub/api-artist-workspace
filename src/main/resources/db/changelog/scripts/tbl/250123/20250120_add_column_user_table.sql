-- liquibase formatted sql
-- changeset kwang:20250123_add_user_menu_table.sql

create table if not exists tbl_user_menu (
    id bigint primary key not null auto_increment comment '인덱스번호',
    user_id bigint not null comment '사용자인덱스id',
    contents_id bigint null comment '컨텐츠인덱스ID',
    menu_type varchar(100) not null comment '메뉴타입'
);

alter table tbl_user_menu add constraint fk_tbl_user_menu_user foreign key (user_id) references tbl_user(id);
alter table tbl_user_menu add constraint fk_tbl_user_menu_content foreign key (contents_id) references tbl_contents(id);
