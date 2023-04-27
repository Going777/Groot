package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import com.groot.backend.dto.response.CommentDTO;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ArticleServiceImpl implements ArticleService{
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ArticleBookmarkRepository articleBookmarkRepository;

    @Override
    public boolean existedArticleId(Long articleId) {
        return articleRepository.existsById(articleId);
    }

    // 게시글 작성
    @Override
    public boolean createArticle(ArticleDTO articleDTO) {
        // 이미지 테이블에 게시글PK + 이미지주소 insert

        // redis에 존재하는지 탐색

        // redis에 태그 insert

        // 태그테이블에 태그 insert
        List<TagEntity> savedTagEntities = new ArrayList<>();
        for(String tag : articleDTO.getTags()){
            TagEntity tagEntity = TagEntity.builder()
                    .name(tag)
                    .build();

            savedTagEntities.add(tagRepository.save(tagEntity));
        }

        // article 테이블에 insert
        ArticleEntity articleEntity = ArticleEntity.builder()
                .category(articleDTO.getCategory())
                .userEntity(userRepository.findById(articleDTO.getUserPK()).orElseThrow())
                .title(articleDTO.getTitle())
                .content(articleDTO.getContent())
                .views(0L)
                .shareStatus(articleDTO.getShareStatus())
                .shareRegion(articleDTO.getShareRegion())
                .build();

        ArticleEntity savedArticleEntity = articleRepository.save(articleEntity);

        // 태크-게시물 테이블에 insert

        for(TagEntity savedTagEntity : savedTagEntities){
            ArticleTagEntity articleTagEntity = ArticleTagEntity.builder()
                    .articleEntity(articleRepository.findById(savedArticleEntity.getId()).orElseThrow())
                    .tagEntity(tagRepository.findById(savedTagEntity.getId()).orElseThrow())
                    .build();

            articleTagRepository.save(articleTagEntity);
        }

        return true;
    }

    @Override
    public ArticleResponseDTO readArticle(Long articleId) {
        // ArticleEntity 조회
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow();
        // UserEntity 조회
        UserEntity userEntity = userRepository.findById(articleEntity.getUserPK()).orElseThrow();
        // tags id 조회
        List<ArticleTagEntity> articleTagEntityList = (List<ArticleTagEntity>) articleTagRepository.findByArticleId(articleId);
        // tag id로 tagEntity 조회
        List<String> tags = new ArrayList<>();
        for(ArticleTagEntity articleTagEntity : articleTagEntityList){
            tags.add(tagRepository.findById(articleTagEntity.getTagId()).orElseThrow().getName());
        }
        // commentEntity 조회
        List<CommentEntity> commentEntityList = (List<CommentEntity>) commentRepository.findByArticleId(articleId);

        List<CommentDTO> comments = new ArrayList<>();
        int commentCnt = 0;
        // commentDTO build
        if(commentEntityList != null){
            for(CommentEntity commentEntity : commentEntityList){
                UserEntity commentUserEntity = userRepository.findById(commentEntity.getUserPK()).orElseThrow();
                CommentDTO commentDTO = CommentDTO.builder()
                        .userPK(commentEntity.getUserPK())
                        .profile(commentUserEntity.getProfile())
                        .content(commentEntity.getContent())
                        .createTime(commentEntity.getCreatedDate())
                        .updateTime(commentEntity.getLastModifiedDate())
                        .build();

                comments.add(commentDTO);
            }
            commentCnt = comments.size();
        }




        // bookmark 여부 조회
        // 복합키 사용을 위한 id 등록
        ArticleBookmarkEntityPK articleBookmarkEntityPK = new ArticleBookmarkEntityPK();
        articleBookmarkEntityPK.setUserEntity(userEntity.getId());
        articleBookmarkEntityPK.setArticleEntity(articleId);

        boolean bookmark;
        if(articleBookmarkRepository.findById(articleBookmarkEntityPK).isPresent()){
            bookmark = false;
        }else bookmark = true;

        ArticleResponseDTO articleResponseDTO = ArticleResponseDTO.builder()
                .category(articleEntity.getCategory())
                .imgs(null)
                .userPK(articleEntity.getUserPK())
                .nickName(userEntity.getNickName())
                .profile(userEntity.getProfile())
                .title(articleEntity.getTitle())
                .tags(tags)
                .views(articleEntity.getViews()+1)
                .commentCnt(commentCnt)
                .bookmark(bookmark)
                .shareRegion(articleEntity.getShareRegion())
                .content(articleEntity.getContent())
                .shareStatus(articleEntity.getShareStatus())
                .createTime(articleEntity.getCreatedDate())
                .updateTime(articleEntity.getLastModifiedDate())
                .comments(comments)
                .build();

        // image 조회

        return articleResponseDTO;
    }


}
