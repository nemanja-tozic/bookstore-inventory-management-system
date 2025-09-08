package com.azul.bookstoreinventorymanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.azul.bookstoreinventorymanagementsystem.AbstractTest;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookUpdateRequestDto;
import com.azul.bookstoreinventorymanagementsystem.exception.EntityAlreadyExistsException;
import com.azul.bookstoreinventorymanagementsystem.exception.EntityNotFoundException;
import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookPrice;
import com.azul.bookstoreinventorymanagementsystem.model.PublisherEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BookServiceTest extends AbstractTest {

  @Autowired
  private BookService bookService;

  @Test
  public void testInsertBook() {
    final var request = new BookInsertRequestDto();
    request.setIsbn("1");
    request.setTitle("title");
    request.setPublisher("publisher");
    request.setPriceAmount(31.2);
    request.setPriceCurrency("EUR");
    request.setAuthors(List.of("author1", "author2"));
    request.setGenres(List.of("HORROR"));

    final var result = bookService.insertBook(request);
    assertThat(result.getId()).isNotNull();
    assertThat(result.getIsbn()).isEqualTo(request.getIsbn());
    assertThat(result.getTitle()).isEqualTo(request.getTitle());
    assertThat(result.getPublisher().getName()).isEqualTo(request.getPublisher());
    assertThat(result.getAuthors().stream().map(AuthorEntity::getName).toList()).containsAll(request.getAuthors());
    assertThat(result.getPrice().getAmount()).isEqualTo(request.getPriceAmount());
    assertThat(result.getPrice().getCurrency()).isEqualTo(request.getPriceCurrency());
    assertThat(result.getGenres().get(0).getName()).isEqualTo(request.getGenres().get(0));
  }

  @Test
  public void insertBook_invalidGenre() {
    final var request = new BookInsertRequestDto();
    request.setIsbn("1");
    request.setTitle("title");
    request.setPublisher("publisher");
    request.setPriceAmount(31.2);
    request.setPriceCurrency("EUR");
    request.setAuthors(List.of("author1", "author2"));
    request.setGenres(List.of("INVALID"));

    assertThrows(EntityNotFoundException.class, () -> bookService.insertBook(request));
  }

  @Test
  public void insertBook_isbnExists() {
    final var request = new BookInsertRequestDto();
    request.setIsbn("1");
    request.setTitle("title");
    request.setPublisher("publisher");
    request.setPriceAmount(31.2);
    request.setPriceCurrency("EUR");
    request.setAuthors(List.of("author1", "author2"));
    request.setGenres(List.of("HORROR"));

    bookService.insertBook(request);
    assertThrows(EntityAlreadyExistsException.class, () -> bookService.insertBook(request));
  }

  @Test
  public void updateBook() {
    final var book = sampleBookEntity("123");
    final var request = new BookUpdateRequestDto();
    request.setTitle("title");
    request.setPublisher("publisher");
    request.setPriceAmount(31.2);
    request.setPriceCurrency("EUR");
    request.setAuthors(List.of("author1", "author2"));
    request.setGenres(List.of("HORROR"));

    final var result = bookService.updateBook(book.getId(), request);
    assertThat(result.getId()).isNotNull();
    assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
    assertThat(result.getTitle()).isEqualTo(request.getTitle());
    assertThat(result.getPublisher().getName()).isEqualTo(request.getPublisher());
    assertThat(result.getAuthors().stream().map(AuthorEntity::getName).toList()).containsAll(request.getAuthors());
    assertThat(result.getPrice().getAmount()).isEqualTo(request.getPriceAmount());
    assertThat(result.getPrice().getCurrency()).isEqualTo(request.getPriceCurrency());
    assertThat(result.getGenres().get(0).getName()).isEqualTo(request.getGenres().get(0));

    assertThat(publisherJpaRepository.findByName(book.getPublisher().getName())).isEmpty();
    assertThat(authorJpaRepository.findByName(book.getAuthors().get(0).getName())).isEmpty();
  }

  @Test
  public void updateBook_noStraggler() {
    final var book1 = sampleBookEntity("123");
    sampleBookEntity("321");
    final var request = new BookUpdateRequestDto();
    request.setTitle("title");
    request.setPublisher("publisher");
    request.setPriceAmount(31.2);
    request.setPriceCurrency("EUR");
    request.setAuthors(List.of("author2", "John", "Bob"));
    request.setGenres(List.of("HORROR"));

    final var result = bookService.updateBook(book1.getId(), request);
    assertThat(result.getId()).isNotNull();
    assertThat(result.getIsbn()).isEqualTo(book1.getIsbn());
    assertThat(result.getTitle()).isEqualTo(request.getTitle());
    assertThat(result.getPublisher().getName()).isEqualTo(request.getPublisher());
    assertThat(result.getAuthors().stream().map(AuthorEntity::getName).toList()).containsAll(request.getAuthors());
    assertThat(result.getPrice().getAmount()).isEqualTo(request.getPriceAmount());
    assertThat(result.getPrice().getCurrency()).isEqualTo(request.getPriceCurrency());
    assertThat(result.getGenres().get(0).getName()).isEqualTo(request.getGenres().get(0));

    assertThat(publisherJpaRepository.findByName(book1.getPublisher().getName())).isNotEmpty();
    assertThat(authorJpaRepository.findByName(book1.getAuthors().get(0).getName())).isNotEmpty();
  }

  @Test
  public void updateBook_nonExisting() {
    final var request = new BookUpdateRequestDto();
    request.setTitle("title");
    request.setPublisher("publisher");
    request.setPriceAmount(31.2);
    request.setPriceCurrency("EUR");
    request.setAuthors(List.of("author2", "John", "Bob"));
    request.setGenres(List.of("HORROR"));

    assertThrows(EntityNotFoundException.class, () -> bookService.updateBook(1L, request));
  }

  @Test
  public void deleteBook() {
    final var book = sampleBookEntity("123");

    bookService.deleteBook(book.getId());

    assertThat(publisherJpaRepository.findByName(book.getPublisher().getName())).isEmpty();
    assertThat(authorJpaRepository.findByName(book.getAuthors().get(0).getName())).isEmpty();
  }

  @Test
  public void deleteBook_noStraggler() {
    final var book = sampleBookEntity("123");
    sampleBookEntity("321");

    bookService.deleteBook(book.getId());

    assertThat(publisherJpaRepository.findByName(book.getPublisher().getName())).isNotEmpty();
    assertThat(authorJpaRepository.findByName(book.getAuthors().get(0).getName())).isNotEmpty();
  }

  private BookEntity sampleBookEntity(String isbn) {
    final var author1 = authorJpaRepository.findByName("John").orElse(new AuthorEntity());
    author1.setName("John");
    authorJpaRepository.save(author1);

    final var author2 = authorJpaRepository.findByName("Luis").orElse(new AuthorEntity());
    author2.setName("Luis");
    authorJpaRepository.save(author2);

    final var publisher = publisherJpaRepository.findByName("Sample publisher").orElse(new PublisherEntity());
    publisher.setName("Sample publisher");
    publisherJpaRepository.save(publisher);

    final var genre = genreJpaRepository.findByName("HORROR").orElseThrow();

    final var bookPrice = new BookPrice();
    bookPrice.setAmount(52.99);
    bookPrice.setCurrency("EUR");

    final var bookEntity = new BookEntity();
    bookEntity.setIsbn(isbn);
    bookEntity.setTitle("Sample title");
    bookEntity.setAuthors(new ArrayList<>(List.of(author1, author2)));
    bookEntity.setPublisher(publisher);
    bookEntity.setGenres(new ArrayList<>(List.of(genre)));
    bookEntity.setPrice(bookPrice);

    return bookJpaRepository.save(bookEntity);
  }
}
