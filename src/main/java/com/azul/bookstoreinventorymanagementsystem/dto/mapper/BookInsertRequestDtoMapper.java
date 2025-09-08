package com.azul.bookstoreinventorymanagementsystem.dto.mapper;

import com.azul.bookstoreinventorymanagementsystem.dto.request.BookInsertRequestDto;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookInsertRequestDtoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "publisher", ignore = true)
  @Mapping(target = "authors", ignore = true)
  @Mapping(target = "genres", ignore = true)
  @Mapping(target = "price.currency", source = "bookInsertRequestDto.priceCurrency")
  @Mapping(target = "price.amount", source = "bookInsertRequestDto.priceAmount")
  BookEntity toBookEntity(BookInsertRequestDto bookInsertRequestDto);
}
