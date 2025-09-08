create sequence user_id_seq increment 1 start 1;
create table user_details (
    id bigint not null primary key default nextval('user_id_seq'),
    username text not null,
    password text not null
);

create table user_role (
    user_id bigint not null,
    role text not null,
    constraint user_role_pk primary key (user_id, role)
);
