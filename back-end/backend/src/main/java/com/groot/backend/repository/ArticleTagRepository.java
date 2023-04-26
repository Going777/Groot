package com.groot.backend.repository;

import com.groot.backend.entity.ArticleTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleTagRepository extends JpaRepository<ArticleTagEntity, Long> {
}
