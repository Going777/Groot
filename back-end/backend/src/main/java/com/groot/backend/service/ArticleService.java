package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.request.BookmarkDTO;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import org.springframework.data.domain.Page;

public interface ArticleService {
    boolean existedArticleId(Long articleId);
    boolean createArticle(ArticleDTO articleDTO, String[] imgPaths);
    ArticleResponseDTO readArticle(Long articleId);
    boolean updateArticle(ArticleDTO articleDTO, String[] imgPaths);
    void deleteArticle(Long articleId);
    Page<ArticleListDTO> readArticleList(String category, Integer page, Integer size);
    void updateBookMark(BookmarkDTO bookmarkDTO);
    Page<ArticleListDTO> filterRegion(String[] region,Integer page, Integer size);
    Page<ArticleListDTO> searchArticle(String keyword,Integer page, Integer size);
}
