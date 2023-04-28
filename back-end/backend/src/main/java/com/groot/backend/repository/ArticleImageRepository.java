package com.groot.backend.repository;

import com.groot.backend.entity.ArticleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImageEntity, Long> {
    List<ArticleImageEntity> findAllById(Long articleId);
}
