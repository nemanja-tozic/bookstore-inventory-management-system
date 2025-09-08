package com.azul.bookstoreinventorymanagementsystem.repository;

import com.azul.bookstoreinventorymanagementsystem.model.PublisherEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherJpaRepository extends JpaRepository<PublisherEntity, Long> {

  Optional<PublisherEntity> findByName(String name);

  @Query(nativeQuery = true, value="""
    select count(*)
    from book
    where publisher_id = :publisherId
  """)
  int countBooks(Long publisherId);
}
