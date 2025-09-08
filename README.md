# Bookstore Inventory Management System

Service is responsible for creating, updating and searching through books.

## Features

- API endpoint for storing, updating and deleting books (see [API endpoints](#api-endpoints)).
- API endpoint for searching through books with multiple criteria (see [API endpoints](#api-endpoints)).
- Data storage in PostgreSQL database.

## Starting the application

### Requirements:

- Java 17
- Gradle
- Docker

### Starting docker services

Postgres database is started as a docker container. To start all the necessary dependencies, run the
following command:

```
docker compose -f docker-compose.yml up -d
```

### Starting the application

First build the application itself, via:

```
./gradlew build
```

Then run the application itself. The dev profile will create initial data for testing.

```
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## API endpoints

### Authentication

To use the API endpoints, you must first be authenticated. If the application is ran with
dev profile, there are 2 sample users available.
- user/password
- admin/password

They can be authenticated via basic authentication. 

### Creating a book
**Required role: ADMIN**

Sample curl for inserting a book:
```
curl --location 'localhost:8080/book' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic ...' \
--data '{
  "isbn": "123456",
  "title": "title",
  "publisher": "publisher",
  "genres": ["HORROR"],
  "authors": ["author"],
  "priceAmount": 0,
  "priceCurrency": "EUR"
}'
```
Parameters:
- body: The inserted book data

Publishers and authors are fetched or created in the database, as there is currently
no separate manager for that data.

While authors and publishers can be any value, genre is statically defined and is expected
to be one of the predefined values. This is to avoid genre having overly similar values.

### Updating a book

**Required role: ADMIN**

Sample curl for updating a book:
```
curl --location -request PUT 'localhost:8080/book/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic ...' \
--data '{
  "title": "title",
  "publisher": "publisher",
  "genres": ["HORROR"],
  "authors": ["author"],
  "priceAmount": 0,
  "priceCurrency": "EUR"
}'
```
Parameters:
- id (path variable): The ID being updated
- body: The updated book data

If by updating the publisher or authors, the previous publisher and/or author
is not present in any dataset, they will be deleted. This is to prevent overflow of data,
especially as there is not separate manager of publishers and authors.

Not all of the inserted information is updatable. In this case, the ISBN
is immutable data and hence cannot be updated once the book is created.

While authors and publishers can be any value, genre is statically defined and is expected
to be one of the predefined values.

### Delete a book

**Required role: ADMIN**

Sample curl for updating a book:
```
curl --location -request DELETE 'localhost:8080/book/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic ...'
```
Parameters:
- id (path variable): The ID being deleted

If by deleting the book, the previous publisher and/or authors
are not present in any other books, they will be deleted. This is to prevent overflow of data,
especially as there is not separate manager of publishers and authors.

### Searching books

**Required role: USER**

Sample curl for searching books:
```
curl --location 'localhost:8080/book/search?title=&author=&genre=&page=&size=' \
--header 'Authorization: Basic ...'
```
Parameters:
- title (request param): The title, or part of the title being searched.
- author (request param): The authors name, or part of it being searched.
- genre (request param): Exact name of the genre being searched.
- page (request param): Page being fetched (starts from 0)
- size (request param): Size of the page (default is 20)

The genre is being validated that it is a valid predefined values.

## Areas of improvement

- Currently publisher and author tables aren't managed and books can create any kind of author or 
publisher. Improvement is advised to create separate controllers for managing them to have higher
control of data.
  - There is also low control of similarity of data, ie. we can end up with "R. R. Tolkien"
  and "John Ronald Reuel Tolkien". 
- Currently genre is statically defined to set values. This data should be more easily extendable
with its own manager (controllers).
- Currently there is only one layer of genres, but certain genres can have subgenres, ie. HORROR
is a genre, but there could be multiple subgenres, ie. SLASHER or MURDER_MISTERY
- Currently authentication data is stored only in the database and isn't extendable. Improvement is
advised to allow better control of authentication.
