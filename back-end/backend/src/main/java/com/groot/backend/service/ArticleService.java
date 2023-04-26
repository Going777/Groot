package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.entity.ArticleEntity;

public interface ArticleService {
    boolean createArticle(ArticleDTO articleDTO);
}
