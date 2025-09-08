package com.azul.bookstoreinventorymanagementsystem.controller;

import static com.azul.bookstoreinventorymanagementsystem.controller.BookSearchController.DEFAULT_PAGE_SIZE;

import com.azul.bookstoreinventorymanagementsystem.AbstractTest;
import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookPrice;
import com.azul.bookstoreinventorymanagementsystem.model.PublisherEntity;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class BookSearchControllerTest extends AbstractTest {

  @Test
  void testSearchAll() {
    final var books = insertBookEntities();

    asUser()
        .when()
        .get("book/search")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", Matchers.hasSize(DEFAULT_PAGE_SIZE))
        .body("number", Matchers.equalTo(0))
        .body("size", Matchers.equalTo(DEFAULT_PAGE_SIZE))
        .body("totalElements",  Matchers.equalTo(books.size()));
  }

  @Test
  void testSearchAllSecondPage() {
    final var books = insertBookEntities();

    asUser()
        .when()
        .get("book/search?page=1")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", Matchers.hasSize(DEFAULT_PAGE_SIZE))
        .body("number", Matchers.equalTo(1))
        .body("size", Matchers.equalTo(DEFAULT_PAGE_SIZE))
        .body("totalElements",  Matchers.equalTo(books.size()));
  }

  @Test
  void testSearchTitle() {
    insertBookEntities();

    asUser()
        .when()
        .get("book/search?title=Title 1")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", Matchers.hasSize(11))
        .body("number", Matchers.equalTo(0))
        .body("size", Matchers.equalTo(DEFAULT_PAGE_SIZE))
        .body("totalElements",  Matchers.equalTo(11));
  }

  @Test
  void testSearchAuthor() {
    insertBookEntities();

    asUser()
        .when()
        .get("book/search?author=John")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", Matchers.hasSize(20))
        .body("number", Matchers.equalTo(0))
        .body("size", Matchers.equalTo(DEFAULT_PAGE_SIZE))
        .body("totalElements",  Matchers.equalTo(25));
  }

  @Test
  void testSearchGenre() {
    insertBookEntities();

    asUser()
        .when()
        .get("book/search?genre=MAGICAL_REALISM")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", Matchers.hasSize(20))
        .body("number", Matchers.equalTo(0))
        .body("size", Matchers.equalTo(DEFAULT_PAGE_SIZE))
        .body("totalElements",  Matchers.equalTo(33));
  }

  @Test
  void testSearchGenre_invalidValue() {
    insertBookEntities();

    asUser()
        .when()
        .get("book/search?genre=INVALID")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  private List<BookEntity> insertBookEntities() {
    String[] authors = {"John", "Luisa", "Bob", "Elly"};
    String[] publishers = {"Publisher 1", "Publisher 2", "The best publisher"};
    String[] genres = {"ROMANCE_NOVEL", "MAGICAL_REALISM", "SUSPENSE"};

    List<BookEntity> bookEntities = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      final var bookEntity = sampleBookEntity(
          String.valueOf(1000000 + i),
          "Title " + i,
          publishers[i % publishers.length],
          genres[i % genres.length],
          authors[i % authors.length]);
      bookEntities.add(bookEntity);
    }
    return bookEntities;
  }

  private BookEntity sampleBookEntity(String isbn, String title, String publisher, String genre, String author) {
    final var authorEntity = authorJpaRepository.findByName(author).orElse(new AuthorEntity());
    authorEntity.setName(author);
    authorJpaRepository.save(authorEntity);

    final var publisherEntity = publisherJpaRepository.findByName(publisher).orElse(new PublisherEntity());
    publisherEntity.setName(publisher);
    publisherJpaRepository.save(publisherEntity);

    final var genreEntity = genreJpaRepository.findByName(genre).orElseThrow();

    final var bookPrice = new BookPrice();
    bookPrice.setAmount(52.99);
    bookPrice.setCurrency("EUR");

    final var bookEntity = new BookEntity();
    bookEntity.setTitle(title);
    bookEntity.setIsbn(isbn);
    bookEntity.setAuthors(new ArrayList<>(List.of(authorEntity)));
    bookEntity.setPublisher(publisherEntity);
    bookEntity.setGenres(new ArrayList<>(List.of(genreEntity)));
    bookEntity.setPrice(bookPrice);

    return bookJpaRepository.save(bookEntity);
  }
}
