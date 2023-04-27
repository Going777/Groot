package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;

public interface ArticleService {
    boolean existedArticleId(Long articleId);
    boolean createArticle(ArticleDTO articleDTO);
    ArticleResponseDTO readArticle(Long articleId);

    boolean updateArticle(ArticleDTO articleDTO);

}
