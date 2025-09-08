package com.azul.bookstoreinventorymanagementsystem.repository;

import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, Long>, PagingAndSortingRepository<BookEntity, Long> {

  Optional<BookEntity> findByIsbn(String isbn);
}
