package com.azul.bookstoreinventorymanagementsystem.controller;

import com.azul.bookstoreinventorymanagementsystem.dto.mapper.BookResponseDtoMapper;
import com.azul.bookstoreinventorymanagementsystem.dto.response.BookResponseDto;
import com.azul.bookstoreinventorymanagementsystem.service.BookSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/book")
public class BookSearchController {
  public static final Integer DEFAULT_PAGE_SIZE = 20;

  private final BookResponseDtoMapper bookResponseDtoMapper;
  private final BookSearchService bookSearchService;

  @GetMapping("/search")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Page<BookResponseDto>> searchBooks(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String author,
      @RequestParam(required = false) String genre,
      @PageableDefault(size = 20) Pageable pageable) {

    final var books = bookSearchService.search(title, author, genre, pageable);
    return ResponseEntity.ok(books.map(bookResponseDtoMapper::toBookResponseDto));
  }
}
