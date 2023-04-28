package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ArticleService {
    boolean existedArticleId(Long articleId);
    boolean createArticle(ArticleDTO articleDTO, String[] imgPaths);
    ArticleResponseDTO readArticle(Long articleId);
    boolean updateArticle(ArticleDTO articleDTO, String[] imgPaths);
    void deleteArticle(Long articleId);
    Page<ArticleListDTO> readArticleList(String category, Integer page, Integer size);

}
