package com.azul.bookstoreinventorymanagementsystem.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BookPrice {

  private Double amount;
  private String currency;
}
