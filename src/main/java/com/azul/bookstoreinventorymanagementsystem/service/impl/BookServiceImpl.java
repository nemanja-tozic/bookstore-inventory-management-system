package com.azul.bookstoreinventorymanagementsystem.service.impl;

import com.azul.bookstoreinventorymanagementsystem.dto.mapper.BookInsertRequestDtoMapper;
import com.azul.bookstoreinventorymanagementsystem.dto.mapper.BookUpdateRequestDtoMapper;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookUpdateRequestDto;
import com.azul.bookstoreinventorymanagementsystem.exception.EntityAlreadyExistsException;
import com.azul.bookstoreinventorymanagementsystem.exception.EntityNotFoundException;
import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookPrice;
import com.azul.bookstoreinventorymanagementsystem.model.GenreEntity;
import com.azul.bookstoreinventorymanagementsystem.model.PublisherEntity;
import com.azul.bookstoreinventorymanagementsystem.repository.AuthorJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.BookJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.GenreJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.PublisherJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.service.BookService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookServiceImpl implements BookService {

  private final AuthorJpaRepository authorJpaRepository;
  private final BookJpaRepository bookJpaRepository;
  private final GenreJpaRepository genreJpaRepository;
  private final PublisherJpaRepository publisherJpaRepository;

  private final BookInsertRequestDtoMapper bookInsertRequestDtoMapper;
  private final BookUpdateRequestDtoMapper bookUpdateRequestDtoMapper;

  @Override
  @Transactional
  public BookEntity insertBook(BookInsertRequestDto request) {
    if (bookJpaRepository.findByIsbn(request.getIsbn()).isPresent()) {
      throw new EntityAlreadyExistsException("Book with ISBN " + request.getIsbn() + " already exists");
    }

    final var book = bookInsertRequestDtoMapper.toBookEntity(request);

    book.setPublisher(getOrCreatePublisher(request.getPublisher()));
    book.setAuthors(request.getAuthors().stream()
        .map(this::getOrCreateAuthor).toList());
    book.setGenres(request.getGenres().stream()
        .map(this::getGenre).toList());

    return bookJpaRepository.save(book);
  }

  @Override
  @Transactional
  public BookEntity updateBook(Long id, BookUpdateRequestDto request) {
    final var book = bookJpaRepository.findById(id)
        .map(b -> bookUpdateRequestDtoMapper.toUpdatedBookEntity(b, request))
        .orElseThrow(() -> new EntityNotFoundException("Book with id " + id + " not found"));

    book.setGenres(getGenres(request.getGenres()));

    Optional<PublisherEntity> oldPublisher = Optional.empty();
    if (!book.getPublisher().getName().equals(request.getPublisher())) {
      oldPublisher = Optional.of(book.getPublisher());
      book.setPublisher(getOrCreatePublisher(request.getPublisher()));
    }

    final var oldAuthors = book.getAuthors();
    final var newAuthors = getOrCreateAuthors(request.getAuthors());
    final var newAuthorIds = newAuthors.stream().map(AuthorEntity::getId).toList();
    final var unusedAuthors = oldAuthors.stream()
        .filter(oa -> !newAuthorIds.contains(oa.getId())).toList();
    if (newAuthors.size() != oldAuthors.size() || !unusedAuthors.isEmpty()) {
      book.setAuthors(newAuthors);
    }

    final var result = bookJpaRepository.save(book);

    oldPublisher.ifPresent(this::removeStragglingPublisher);
    removeStragglingAuthors(unusedAuthors);

    return result;
  }

  @Override
  @Transactional
  public void deleteBook(Long id) {
    final var book = bookJpaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Book with id " + id + " not found"));
    bookJpaRepository.delete(book);

    removeStragglingAuthors(book.getAuthors());
    removeStragglingPublisher(book.getPublisher());
  }

  private PublisherEntity getOrCreatePublisher(String publisherName) {
    // As the requirement states to create only Book CRUD controller, added a publisher "get or
    //   create" method, but more likely the better approach is to have a separate controller for
    //   managing publishers.
    // In that case the request object wouldn't contain "publisher" (name), but "publisherId".
    return publisherJpaRepository.findByName(publisherName)
        .orElseGet(() -> {
          final var publisher = new PublisherEntity();
          publisher.setName(publisherName);
          return publisherJpaRepository.save(publisher);
        });
  }

  private List<AuthorEntity> getOrCreateAuthors(List<String> authorNames) {
    return new ArrayList<>(authorNames.stream().map(this::getOrCreateAuthor).toList());
  }

  private AuthorEntity getOrCreateAuthor(String authorName) {
    // As the requirement states to create only Book CRUD controller, added an author "get or
    //   create" method, but more likely the better approach is to have a separate controller for
    //   managing authors.
    // In that case the request object wouldn't contain "author" (name), but "authodId".
    // The reason behind this is that currently the system might end up with similar authors, ie.
    //   "J.R.R. Tolkien" and "John Ronald Reuel Tolkien"
    return authorJpaRepository.findByName(authorName)
        .orElseGet(() -> {
          final var author = new AuthorEntity();
          author.setName(authorName);
          return authorJpaRepository.save(author);
        });
  }

  private List<GenreEntity> getGenres(List<String> genres) {
    return new ArrayList<>(genres.stream().map(this::getGenre).toList());
  }

  private GenreEntity getGenre(String genreName) {
    // In the case of genre it might be a smarter idea to not do a "get or create" method as it's
    //   most likely more importantly statically defined and is something that should be validated.
    return genreJpaRepository.findByName(genreName.toUpperCase().strip())
        .orElseThrow(() -> new EntityNotFoundException("Genre with name " + genreName + " not found"));
  }

  private void removeStragglingPublisher(PublisherEntity publisher) {
    // Since the current implementation does not have a separate manager of publishers, it might be
    //   a good idea to delete "straggling" publishers which do not have associated books in the database.
    final var publisherBookCount = publisherJpaRepository.countBooks(publisher.getId());
    if (publisherBookCount == 0) {
      publisherJpaRepository.delete(publisher);
    }
  }

  private void removeStragglingAuthors(List<AuthorEntity> authors) {
    // Since the current implementation does not have a separate manager of authors, it might be
    //   a good idea to delete "straggling" authors which do not have associated books in the database.
    authors.forEach(author -> {
      final var bookCount = authorJpaRepository.countBooks(author.getId());
      if (bookCount == 0) {
        authorJpaRepository.delete(author);
      }
    });
  }
}
