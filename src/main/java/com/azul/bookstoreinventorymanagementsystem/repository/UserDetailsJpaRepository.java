package com.azul.bookstoreinventorymanagementsystem.repository;

import com.azul.bookstoreinventorymanagementsystem.model.UserDetailsEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsJpaRepository extends JpaRepository<UserDetailsEntity, Long> {

  Optional<UserDetailsEntity> findByUsername(String username);
}
