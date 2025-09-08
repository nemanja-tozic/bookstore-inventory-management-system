package com.azul.bookstoreinventorymanagementsystem.service.impl;

import com.azul.bookstoreinventorymanagementsystem.exception.EntityNotFoundException;
import com.azul.bookstoreinventorymanagementsystem.model.BookEntity;
import com.azul.bookstoreinventorymanagementsystem.repository.GenreJpaRepository;
import com.azul.bookstoreinventorymanagementsystem.service.BookSearchService;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookSearchServiceImpl implements BookSearchService {

  private final GenreJpaRepository genreJpaRepository;
  private final EntityManager entityManager;

  @Override
  public Page<BookEntity> search(String title, String author, String genre, Pageable pageable) {
    validate(genre);

    final var cb = entityManager.getCriteriaBuilder();
    final var cq = cb.createQuery(BookEntity.class);
    final var root = cq.from(BookEntity.class);

    final var predicates = getPredicates(cb, root, title, author, genre);
    cq.select(root)
        .where(cb.and(predicates.toArray(new Predicate[0])))
        .distinct(true)
        .orderBy(cb.asc(root.get("title")));

    final var query = entityManager.createQuery(cq);
    query.setFirstResult((int) pageable.getOffset());
    query.setMaxResults(pageable.getPageSize());
    final var content = query.getResultList();

    final var countCq = cb.createQuery(Long.class);
    final var countRoot = countCq.from(BookEntity.class);
    final var countPredicates = getPredicates(cb, countRoot, title, author, genre);

    countCq.select(cb.countDistinct(countRoot))
        .where(cb.and(countPredicates.toArray(new Predicate[0])));
    final Long total = entityManager.createQuery(countCq).getSingleResult();

    return new PageImpl<>(content, pageable, total);

  }

  private void validate(String genre) {
    if (StringUtils.isNotBlank(genre)) {
      genreJpaRepository.findByName(genre)
          .orElseThrow(() -> new EntityNotFoundException(genre));
    }
  }

  private List<Predicate> getPredicates(
      CriteriaBuilder cb, Root<BookEntity> root, String title, String author, String genre) {
    final var predicates = new ArrayList<Predicate>();

    if (StringUtils.isNotBlank(title)) {
      predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase().strip() + "%"));
    }

    if (StringUtils.isNotBlank(author)) {
      final var authorJoin = root.join("authors", JoinType.LEFT);
      predicates.add(cb.like(cb.lower(authorJoin.get("name")), "%" + author.toLowerCase().strip() + "%"));
    }

    if (StringUtils.isNotBlank(genre)) {
      final var genreJoin = root.join("genres", JoinType.LEFT);
      predicates.add(cb.equal(genreJoin.get("name"), genre.toUpperCase().strip()));
    }

    return predicates;
  }
}
