package com.azul.bookstoreinventorymanagementsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class BookUpdateRequestDto {

  @NotEmpty(message = "Title is required.")
  private String title;

  @NotEmpty(message = "Book publisher is mandatory.")
  private String publisher;

  @NotEmpty(message = "Book must be contained in at least one genre.")
  private List<String> genres;

  @NotEmpty(message = "Book author is mandatory.")
  private List<String> authors;

  @NotNull(message = "Book price is mandatory.")
  @Min(value = 0, message = "Price amount must be a positive value.")
  private Double priceAmount;

  @NotNull(message = "Book price currency is mandatory.")
  @Size(min = 3, max = 3, message = "Currency must be in ISO format.")
  private String priceCurrency;
}
