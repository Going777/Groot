package com.groot.backend.repository;

import com.groot.backend.entity.ArticleBookmarkEntity;
import com.groot.backend.entity.ArticleBookmarkEntityPK;
import com.groot.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleBookmarkRepository extends JpaRepository<ArticleBookmarkEntity, ArticleBookmarkEntityPK> {
}
