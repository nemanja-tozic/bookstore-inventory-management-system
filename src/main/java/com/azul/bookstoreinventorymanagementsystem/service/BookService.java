package com.azul.bookstoreinventorymanagementsystem.service;

import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookUpdateRequestDto;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;

public interface BookService {

  BookEntity insertBook(BookInsertRequestDto request);

  BookEntity updateBook(Long id, BookUpdateRequestDto request);

  void deleteBook(Long id);
}
