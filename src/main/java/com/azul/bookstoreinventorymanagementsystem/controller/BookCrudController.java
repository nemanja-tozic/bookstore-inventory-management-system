package com.azul.bookstoreinventorymanagementsystem.controller;

import com.azul.bookstoreinventorymanagementsystem.dto.mapper.BookResponseDtoMapper;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookUpdateRequestDto;
import com.azul.bookstoreinventorymanagementsystem.dto.response.BookResponseDto;
import com.azul.bookstoreinventorymanagementsystem.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/book")
public class BookCrudController {

  private final BookService bookService;
  private final BookResponseDtoMapper bookResponseDtoMapper;

  @PostMapping("")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BookResponseDto> insertBook(
      @RequestBody @Valid BookInsertRequestDto request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        bookResponseDtoMapper.toBookResponseDto(bookService.insertBook(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BookResponseDto> updateBook(
      @PathVariable Long id, @RequestBody @Valid BookUpdateRequestDto request) {
    return ResponseEntity.ok(
        bookResponseDtoMapper.toBookResponseDto(bookService.updateBook(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
  }
}
