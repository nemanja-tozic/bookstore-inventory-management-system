package com.azul.bookstoreinventorymanagementsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.azul.bookstoreinventorymanagementsystem.AbstractTest;
import com.azul.bookstoreinventorymanagementsystem.exception.EntityNotFoundException;
import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookPrice;
import com.azul.bookstoreinventorymanagementsystem.model.PublisherEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BookSearchServiceTest extends AbstractTest {

  @Autowired
  private BookSearchService bookSearchService;

  @Test
  public void testShouldFindAllBooks() {
    final var book1 = sampleBookEntity("1", "aaa", "pub_aaa", "HORROR", "author_aaa");
    final var book2 = sampleBookEntity("2", "bbb", "pub_aaa", "HORROR", "author_bbb");
    final var book3 = sampleBookEntity("3", "ccc", "pub_bbb", "HORROR", "author_aaa");

    final var all = bookSearchService.search(
        null, null, null, Pageable.ofSize(3));

    assertThat(all.getTotalElements()).isEqualTo(3);
    assertThat(all.getTotalPages()).isEqualTo(1);
    assertThat(all.getContent()).containsExactlyInAnyOrder(book1, book2, book3);
  }

  @Test
  public void testShouldFindAllBooksWithTitle() {
    final var book1 = sampleBookEntity("1", "aaa", "pub_aaa", "HORROR", "author_aaa");
    final var book2 = sampleBookEntity("2", "bab", "pub_aaa", "HORROR", "author_bbb");
    sampleBookEntity("3", "ccc", "pub_bbb", "HORROR", "author_aaa");

    final var all = bookSearchService.search(
        "a", null, null, Pageable.ofSize(3));

    assertThat(all.getTotalElements()).isEqualTo(2);
    assertThat(all.getTotalPages()).isEqualTo(1);
    assertThat(all.getContent()).containsExactlyInAnyOrder(book1, book2);
  }

  @Test
  public void testShouldFindAllBooksWithAuthor() {
    final var book1 = sampleBookEntity("1", "aaa", "pub_aaa", "HORROR", "author_aaa");
    sampleBookEntity("2", "bab", "pub_aaa", "HORROR", "author_bbb");
    final var book3 = sampleBookEntity("3", "ccc", "pub_bbb", "HORROR", "author_aaa");

    final var all = bookSearchService.search(
        null, "AA", null, Pageable.ofSize(3));

    assertThat(all.getTotalElements()).isEqualTo(2);
    assertThat(all.getTotalPages()).isEqualTo(1);
    assertThat(all.getContent()).containsExactlyInAnyOrder(book1, book3);
  }

  @Test
  public void testShouldFindAllBooksWithGenre() {
    sampleBookEntity("1", "aaa", "pub_aaa", "SCIENCE_FICTION", "author_aaa");
    final var book2 = sampleBookEntity("2", "bab", "pub_aaa", "HORROR", "author_bbb");
    final var book3 = sampleBookEntity("3", "ccc", "pub_bbb", "HORROR", "author_aaa");

    final var all = bookSearchService.search(
        null, null, "HORROR", Pageable.ofSize(3));

    assertThat(all.getTotalElements()).isEqualTo(2);
    assertThat(all.getTotalPages()).isEqualTo(1);
    assertThat(all.getContent()).containsExactlyInAnyOrder(book2, book3);
  }

  @Test
  public void testShouldFindAllBooksInvalidGenre() {
    sampleBookEntity("1", "aaa", "pub_aaa", "SCIENCE_FICTION", "author_aaa");
    sampleBookEntity("2", "bab", "pub_aaa", "HORROR", "author_bbb");
    sampleBookEntity("3", "ccc", "pub_bbb", "HORROR", "author_aaa");

    assertThrows(EntityNotFoundException.class, () -> bookSearchService.search(
        null, null, "INVALID", Pageable.ofSize(3)));
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
