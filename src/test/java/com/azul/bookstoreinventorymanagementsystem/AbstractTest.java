package com.azul.bookstoreinventorymanagementsystem;

import com.azul.bookstoreinventorymanagementsystem.model.UserDetailsEntity;
import com.azul.bookstoreinventorymanagementsystem.model.enums.UserRole;
import com.azul.bookstoreinventorymanagementsystem.repository.AuthorJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.BookJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.GenreJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.PublisherJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.repository.UserDetailsJpaRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractTest {

  @LocalServerPort
  protected Integer port;

  @Autowired
  protected AuthorJpaRepository authorJpaRepository;

  @Autowired
  protected PublisherJpaRepository publisherJpaRepository;

  @Autowired
  protected UserDetailsJpaRepository userDetailsJpaRepository;

  @Autowired
  protected BookJpaRepository bookJpaRepository;

  @Autowired
  protected GenreJpaRepository genreJpaRepository;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
      "postgres:17-alpine"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.url", postgres::getJdbcUrl);
    registry.add("spring.flyway.user", postgres::getUsername);
    registry.add("spring.flyway.password", postgres::getPassword);
  }

  @BeforeEach
  void setUp() {
    userDetailsJpaRepository.deleteAll();
    bookJpaRepository.deleteAll();
    authorJpaRepository.deleteAll();
    publisherJpaRepository.deleteAll();

    userDetailsJpaRepository.save(getUserDetails("user", Set.of(UserRole.USER)));
    userDetailsJpaRepository.save(getUserDetails("admin", Set.of(UserRole.ADMIN)));
  }

  private UserDetailsEntity getUserDetails(String username, Set<UserRole> roles) {
    UserDetailsEntity userDetails = new UserDetailsEntity();
    userDetails.setUsername(username);
    userDetails.setPassword(passwordEncoder.encode("password"));
    userDetails.setRoles(roles);
    return userDetails;
  }

  protected RequestSpecification asUser() {
    return RestAssured.given()
        .port(port)
        .auth()
        .preemptive()
        .basic("user", "password")
        .contentType(ContentType.JSON);
  }

  protected RequestSpecification asAdmin() {
    return RestAssured.given()
        .port(port)
        .auth()
        .preemptive()
        .basic("admin", "password")
        .contentType(ContentType.JSON);
  }
}
