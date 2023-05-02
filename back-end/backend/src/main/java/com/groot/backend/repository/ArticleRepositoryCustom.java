package com.groot.backend.repository;

import com.groot.backend.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ArticleRepositoryCustom {
    Page<ArticleEntity> filterRegion(String[] region, PageRequest pageRequest);
    Page<ArticleEntity> search(String keyword, PageRequest pageRequest);
    List<ArticleEntity> findUserSharedArticle(Long userPK, Long articleId);
    Page<ArticleEntity> findAllByUserPK(Long userPK, PageRequest pageRequest);
    List<Long> findBookmarkByUserPK(Long userPK);
    Page<ArticleEntity> findAllById(List<Long> bookmarkList, PageRequest pageRequest);
}
