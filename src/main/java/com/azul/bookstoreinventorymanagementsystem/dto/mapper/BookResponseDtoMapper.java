package com.azul.bookstoreinventorymanagementsystem.dto.mapper;

import com.azul.bookstoreinventorymanagementsystem.dto.response.BookResponseDto;
import com.azul.bookstoreinventorymanagementsystem.model.AuthorEntity;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.model.GenreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookResponseDtoMapper {

  @Mapping(target = "publisher", source = "publisher.name")
  BookResponseDto toBookResponseDto(BookEntity book);

  default String getAuthor(AuthorEntity author) {
    return author.getName();
  }

  default String getGenre(GenreEntity genre) {
    return genre.getName();
  }
}
