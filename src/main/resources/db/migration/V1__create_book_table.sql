create sequence genre_id_seq increment 1 start 1;
create table genre (
    id bigint not null primary key default nextval('genre_id_seq'),
    name text not null unique
);

create sequence author_id_seq increment 1 start 1;
create table author (
    id bigint not null primary key default nextval('author_id_seq'),
    name text not null unique
);

create sequence publisher_id_seq increment 1 start 1;
create table publisher (
    id bigint not null primary key default nextval('publisher_id_seq'),
    name text not null unique
);

create sequence book_id_seq increment 1 start 1;
create table book (
    id bigint not null primary key default nextval('book_id_seq'),
    isbn text not null unique,
    title text not null,
    publisher_id bigint not null,
    price_amount bigint not null,
    price_currency varchar(3) not null,
    constraint book_publisher_fk foreign key (publisher_id) references publisher(id)
);
create index publisher_id_ix on book(publisher_id);

create table book_author (
    book_id bigint not null,
    author_id bigint not null,
    constraint book_author_pk primary key (book_id, author_id),
    constraint book_author_book_id_fk foreign key (book_id) references book(id),
    constraint book_author_author_id_fk foreign key (author_id) references author(id)
);

create table book_genre (
    book_id bigint not null,
    genre_id bigint not null,
    constraint book_genre_pk primary key (book_id, genre_id),
    constraint book_genre_book_id_fk foreign key (book_id) references book(id),
    constraint book_genre_genre_id_fk foreign key (genre_id) references genre(id)
);
