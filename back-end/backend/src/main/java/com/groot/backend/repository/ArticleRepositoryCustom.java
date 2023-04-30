package com.groot.backend.repository;

import com.groot.backend.entity.ArticleEntity;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<ArticleEntity> filterRegion(String[] region);
}
