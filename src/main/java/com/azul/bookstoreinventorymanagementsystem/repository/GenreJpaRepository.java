package com.azul.bookstoreinventorymanagementsystem.repository;

import com.azul.bookstoreinventorymanagementsystem.model.GenreEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreJpaRepository extends JpaRepository<GenreEntity, Long> {

  Optional<GenreEntity> findByName(String name);
}
