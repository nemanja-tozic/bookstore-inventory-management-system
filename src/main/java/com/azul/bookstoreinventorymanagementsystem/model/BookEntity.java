package com.azul.bookstoreinventorymanagementsystem.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "book")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookSequence")
  @SequenceGenerator(name = "bookSequence", sequenceName = "book_id_seq", allocationSize = 1)
  private Long id;

  @Column(unique = true, nullable = false)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @ManyToOne
  @JoinColumn(name = "publisher_id")
  private PublisherEntity publisher;

  @ManyToMany
  @JoinTable(name = "book_author",
    joinColumns = @JoinColumn(name = "book_id"),
    inverseJoinColumns = @JoinColumn(name = "author_id"))
  private List<AuthorEntity> authors;

  @ManyToMany
  @JoinTable(name = "book_genre",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id"))
  private List<GenreEntity> genres;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "currency",
          column = @Column(name = "price_currency", length = 3, nullable = false)),
      @AttributeOverride(name = "amount",
          column = @Column(name = "price_amount", nullable = false))
  })
  private BookPrice price;
}
