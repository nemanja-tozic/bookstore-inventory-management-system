package com.azul.bookstoreinventorymanagementsystem.repository;

import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorJpaRepository extends JpaRepository<AuthorEntity, Long> {

  Optional<AuthorEntity> findByName(String name);

  @Query(nativeQuery = true, value="""
    select count(*)
    from book_author
    where author_id = :authorId
  """)
  int countBooks(Long authorId);
}
