package com.azul.bookstoreinventorymanagementsystem.service;

import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookSearchService {

  Page<BookEntity> search(String title, String author, String genre, Pageable pageable);
}
