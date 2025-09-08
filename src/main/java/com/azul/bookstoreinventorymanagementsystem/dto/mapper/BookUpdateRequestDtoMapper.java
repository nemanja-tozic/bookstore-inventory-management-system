package com.azul.bookstoreinventorymanagementsystem.dto.mapper;

import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.dto.request.BookUpdateRequestDto;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookUpdateRequestDtoMapper {

  @Mapping(target = "title", source = "bookInsertRequestDto.title")
  @Mapping(target = "publisher", source = "bookEntity.publisher")
  @Mapping(target = "authors", source = "bookEntity.authors")
  @Mapping(target = "genres", source = "bookEntity.genres")
  @Mapping(target = "price.currency", source = "bookInsertRequestDto.priceCurrency")
  @Mapping(target = "price.amount", source = "bookInsertRequestDto.priceAmount")
  BookEntity toUpdatedBookEntity(BookEntity bookEntity, BookUpdateRequestDto bookInsertRequestDto);
}
