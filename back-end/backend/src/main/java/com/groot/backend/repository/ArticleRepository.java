package com.groot.backend.repository;

import com.groot.backend.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long>, ArticleRepositoryCustom {
    Page<ArticleEntity> findAllByCategory(String category, PageRequest pageRequest);
}
