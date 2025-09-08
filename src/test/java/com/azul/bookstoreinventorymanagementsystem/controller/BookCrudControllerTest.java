package com.azul.bookstoreinventorymanagementsystem.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.azul.bookstoreinventorymanagementsystem.AbstractTest;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookPrice;
import com.azul.bookstoreinventorymanagementsystem.model.PublisherEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class BookCrudControllerTest extends AbstractTest {

  @Test
  void testInsertBookTest_ok() {
    final var request = getBookUpsertRequestDto();
    asAdmin()
        .body(request)
        .when()
        .post("/book")
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("title", equalTo(request.getTitle()));
  }

  @Test
  void testInsertBookTest_isbnExists() {
    final var book = sampleBookEntity();
    final var request = getBookUpsertRequestDto();
    asAdmin()
        .body(request)
        .when()
        .post("/book")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void testInsertBookTest_emptyTitle() {
    final var request = getBookUpsertRequestDto();
    request.setTitle("");
    asAdmin()
        .body(request)
        .when()
        .post("/book")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void testInsertBookTest_noAuthor() {
    final var request = getBookUpsertRequestDto();
    request.setAuthors(List.of());
    asAdmin()
        .body(request)
        .when()
        .post("/book")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void testInsertBookTest_noPublisher() {
    final var request = getBookUpsertRequestDto();
    request.setPublisher(null);
    asAdmin()
        .body(request)
        .when()
        .post("/book")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void testInsertBookTest_priceNegative() {
    final var request = getBookUpsertRequestDto();
    request.setPriceAmount(-32.3);
    asAdmin()
        .body(request)
        .when()
        .post("/book")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void testUpdateBookTest_ok() {
    final var savedBook = sampleBookEntity();
    final var request = getBookUpsertRequestDto();
    request.setTitle("updated title");
    asAdmin()
        .body(request)
        .when()
        .put("/book/" + savedBook.getId())
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("title", equalTo(request.getTitle()));
  }

  @Test
  void testUpdateBookTest_removedStragglers() {
    final var savedBook = sampleBookEntity();
    final var request = getBookUpsertRequestDto();
    request.setPublisher("different publisher");
    request.setAuthors(List.of("Luis", "Bob"));
    request.setGenres(List.of("PARANORMAL_ROMANCE"));
    asAdmin()
        .body(request)
        .when()
        .put("/book/" + savedBook.getId())
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("authors", equalTo(request.getAuthors()))
        .body("genres", equalTo(request.getGenres()))
        .body("publisher", equalTo(request.getPublisher()));
    // check that straggling author and publisher is removed
    assertThat(authorJpaRepository.findByName("John")).isEmpty();
    assertThat(publisherJpaRepository.findByName("Sample publisher")).isEmpty();
  }

  @Test
  void testDeleteBookTest_ok() {
    final var savedBook = sampleBookEntity();
    asAdmin()
        .when()
        .delete("/book/" + savedBook.getId())
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value());

    assertThat(bookJpaRepository.findById(savedBook.getId())).isEmpty();

    // check that straggling authors and publishers are removed
    assertThat(authorJpaRepository.findByName("John")).isEmpty();
    assertThat(authorJpaRepository.findByName("Luis")).isEmpty();
    assertThat(publisherJpaRepository.findByName("Sample publisher")).isEmpty();
  }

  private BookEntity sampleBookEntity() {
    final var author1 = new AuthorEntity();
    author1.setName("John");
    authorJpaRepository.save(author1);

    final var author2 = new AuthorEntity();
    author2.setName("Luis");
    authorJpaRepository.save(author2);

    final var publisher = new PublisherEntity();
    publisher.setName("Sample publisher");
    publisherJpaRepository.save(publisher);

    final var genre = genreJpaRepository.findByName("HORROR").orElseThrow();

    final var bookPrice = new BookPrice();
    bookPrice.setAmount(52.99);
    bookPrice.setCurrency("EUR");

    final var bookEntity = new BookEntity();
    bookEntity.setIsbn("123456789");
    bookEntity.setTitle("Sample title");
    bookEntity.setAuthors(new ArrayList<>(List.of(author1, author2)));
    bookEntity.setPublisher(publisher);
    bookEntity.setGenres(new ArrayList<>(List.of(genre)));
    bookEntity.setPrice(bookPrice);

    return bookJpaRepository.save(bookEntity);
  }

  private BookInsertRequestDto getBookUpsertRequestDto() {
    final var bookUpsertRequest = new BookInsertRequestDto();
    bookUpsertRequest.setTitle("Sample title");
    bookUpsertRequest.setIsbn("123456789");
    bookUpsertRequest.setPriceAmount(52.99);
    bookUpsertRequest.setPriceCurrency("EUR");
    bookUpsertRequest.setAuthors(List.of("John", "Luis"));
    bookUpsertRequest.setGenres(List.of("HORROR"));
    bookUpsertRequest.setPublisher("Sample publisher");

    return bookUpsertRequest;
  }
}
