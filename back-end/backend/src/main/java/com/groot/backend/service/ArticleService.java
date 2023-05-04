package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.request.BookmarkDTO;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import com.groot.backend.dto.response.TagRankDTO;
import com.groot.backend.dto.response.UserSharedArticleDTO;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.List;

public interface ArticleService {
    List<String> readRegion();
    boolean existedArticleId(Long articleId);
    boolean createArticle(ArticleDTO articleDTO, String[] imgPaths);
    ArticleResponseDTO readArticle(Long articleId, Long userPK);
    @Transactional
    boolean updateArticle(ArticleDTO articleDTO, String[] imgPaths);
    void deleteArticle(Long articleId);
    Page<ArticleListDTO> readArticleList(String category, Long userPK, Integer page, Integer size);
    void updateBookMark(BookmarkDTO bookmarkDTO);
    Page<ArticleListDTO> filterRegion(String[] region, Long userPK,Integer page, Integer size);
    Page<ArticleListDTO> searchArticle(String keyword,Long userPK, Integer page, Integer size);
    List<UserSharedArticleDTO> readUserShared(Long articleId);
    Page<ArticleListDTO> readUserArticles(Long userPK,Integer page, Integer size);
    Page<ArticleListDTO> readUserBookmarks(Long userPK,Integer page, Integer size);
    List<TagRankDTO> readTagRanking();
    // 스케줄러
    void updateTagCountTable();
}
