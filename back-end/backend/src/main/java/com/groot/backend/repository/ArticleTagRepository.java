package com.groot.backend.repository;

import com.groot.backend.entity.ArticleTagEntity;
import com.groot.backend.entity.ArticleTagEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTagEntity, ArticleTagEntityPK> {
    List<ArticleTagEntity> findByArticleId(Long articleId);
}
