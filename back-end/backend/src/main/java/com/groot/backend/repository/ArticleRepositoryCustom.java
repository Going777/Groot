package com.groot.backend.repository;

import com.groot.backend.entity.ArticleEntity;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticleEntity> filterRegion(String[] region);
    List<ArticleEntity> search(String keyword);
    List<ArticleEntity> findUserSharedArticle(Long userPK, Long articleId);
    List<ArticleEntity> findAllByUserPK(Long userPK);
    List<Long> findBookmarkByUserPK(Long userPK);
}
