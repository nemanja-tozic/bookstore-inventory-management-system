package com.azul.bookstoreinventorymanagementsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "author")
public class AuthorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authorSequence")
  @SequenceGenerator(name = "authorSequence", sequenceName = "author_id_seq", allocationSize = 1)
  private Long id;

  private String name;
}
