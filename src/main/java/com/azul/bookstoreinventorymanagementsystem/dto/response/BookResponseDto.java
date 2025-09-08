package com.azul.bookstoreinventorymanagementsystem.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class BookResponseDto {

  private Long id;
  private String isbn;
  private String title;
  private String publisher;
  private List<String> authors;
  private List<String> genres;
  private BookPriceResponseDto price;
}
