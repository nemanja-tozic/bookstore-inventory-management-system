-- sample data for testing
insert into user_details (id, username, password)
values
    (10000, 'user', '$2a$10$C48bXsyQK0OZ5nR2gserh.r8WzHyqZZ.T1wmWxejg2TNLB1U/eTtO'), --password
    (10001, 'admin', '$2a$10$C48bXsyQK0OZ5nR2gserh.r8WzHyqZZ.T1wmWxejg2TNLB1U/eTtO'); --password

insert into user_role (user_id, role)
values
    (10000, 'USER'),
    (10001, 'ADMIN');

-- add sample book data
insert into publisher (id, name)
values
    (10000, 'Penguin Random House'),
    (10001, 'Hachette Book Group');

insert into book (id, isbn, title, publisher_id, price_amount, price_currency)
values
    (10000, '12413251', 'The Window', 10000, 35, 'USD'),
    (10001, '12413232', 'Blue Lock', 10000, 14, 'USD'),
    (10002, '12413242', 'Full of myself', 10000, 25, 'EUR'),
    (10003, '12413512', 'Nia Liston', 10000, 22, 'EUR'),
    (10004, '12413645', 'Guide me home', 10001, 51, 'EUR'),
    (10005, '12413283', 'Madwoman', 10001, 23, 'EUR'),
    (10006, '12418756', 'God shot', 10001, 51, 'USD');

insert into author (id, name)
values
    (10000, 'John Grisham'),
    (10001, 'Liana de la Rosa'),
    (10002, 'Brown'),
    (10003, 'Kabuto Kodai'),
    (10004, 'Umikaze Minamino'),
    (10005, 'Attica Locke'),
    (10006, 'Chelsea Bieker');

insert into book_author (book_id, author_id)
values
    (10000, 10000),
    (10001, 10001),
    (10002, 10002),
    (10003, 10003),
    (10003, 10004),
    (10004, 10005),
    (10005, 10006),
    (10006, 10006);

insert into book_genre (book_id, genre_id)
values
    (10000, (select id from genre where name = 'CLASSIC')),
    (10000, (select id from genre where name = 'LITERARY_FICTION')),
    (10000, (select id from genre where name = 'ADVENTURE_FICTION')),
    (10001, (select id from genre where name = 'SHORT_STORY')),
    (10001, (select id from genre where name = 'FANTASY')),
    (10002, (select id from genre where name = 'CONTEMPORARY_LITERATURE')),
    (10002, (select id from genre where name = 'FANTASY')),
    (10003, (select id from genre where name = 'CRIME')),
    (10003, (select id from genre where name = 'LITERARY_FICTION')),
    (10003, (select id from genre where name = 'MEMOIR')),
    (10004, (select id from genre where name = 'CHILDRENS_LITERATURE')),
    (10004, (select id from genre where name = 'YOUNG_ADULT')),
    (10005, (select id from genre where name = 'HISTORICAL_FICTION')),
    (10005, (select id from genre where name = 'ROMANCE_NOVEL')),
    (10006, (select id from genre where name = 'DRAMA'));
