package com.groot.backend.repository;

import com.groot.backend.entity.ArticleTagEntity;
import com.groot.backend.entity.ArticleTagEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTagEntity, ArticleTagEntityPK> {
    List<ArticleTagEntity> findByArticleId(Long articleId);
    @Query(value = "select t.name, count(tag_id) from articles_tags a join tags t on t.id = a.tag_id group by a.tag_id", nativeQuery = true)
    List<Object[]> findCountByTag();
}
