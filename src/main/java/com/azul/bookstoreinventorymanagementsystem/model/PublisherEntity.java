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
@Table(name = "publisher")
public class PublisherEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "publisherSequence")
  @SequenceGenerator(name = "publisherSequence", sequenceName = "publisher_id_seq", allocationSize = 1)
  private Long id;

  private String name;
}
