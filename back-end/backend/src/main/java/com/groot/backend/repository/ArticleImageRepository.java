package com.groot.backend.repository;

import com.groot.backend.entity.ArticleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleImageRepository extends JpaRepository<ArticleImageEntity, Long> {
    @Query(value = "select * from articles_images where article_id=:articleId", nativeQuery = true)
    List<ArticleImageEntity> findAllByArticleId(@Param("articleId") Long articleId);
}
