package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import com.groot.backend.dto.response.CommentDTO;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        // redis에 새로 insert된 태그 리스트
        String[] newTags = articleDTO.getTags();

        // 태그테이블에 태그 insert
        for(String tag : newTags){
            if(tagRepository.findByName(tag) == null){
                TagEntity tagEntity = TagEntity.builder()
                        .name(tag)
                        .build();

                tagRepository.save(tagEntity);
            }
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
        for(String tag : articleDTO.getTags()){
            ArticleTagEntity articleTagEntity = ArticleTagEntity.builder()
                    .articleEntity(articleRepository.findById(savedArticleEntity.getId()).orElseThrow())
                    .tagEntity(tagRepository.findByName(tag))
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

        // 조회수 업데이트
        ArticleEntity newArticleEntity = ArticleEntity.builder()
                .id(articleEntity.getId())
                .category(articleEntity.getCategory())
                .userEntity(userRepository.findById(articleEntity.getUserPK()).orElseThrow())
                .title(articleEntity.getTitle())
                .content(articleEntity.getContent())
                .views(articleEntity.getViews()+1)
                .shareStatus(articleEntity.getShareStatus())
                .shareRegion(articleEntity.getShareRegion())
                .build();

        articleRepository.save(newArticleEntity);

        // image 조회

        return articleResponseDTO;
    }

    @Override
    public boolean updateArticle(ArticleDTO articleDTO) {
        // 이미지 테이블에 게시글PK + 이미지주소 insert

        // redis에 존재하는지 탐색

        // redis에 태그 insert
        // redis에 새로 insert된 태그 리스트
        String[] tags = articleDTO.getTags();
        List<String> newTags = new ArrayList<>();
        for(String tag : tags){
            if(tagRepository.findByName(tag) == null){
                newTags.add(tag);
            }
        }

        // 태그테이블에 태그 insert
        if(newTags != null){
            for(String tag : newTags){
                TagEntity tagEntity = TagEntity.builder()
                        .name(tag)
                        .build();

                tagRepository.save(tagEntity);
            }
        }


        // article 테이블에 update
        ArticleEntity articleEntity = articleRepository.findById(articleDTO.getArticleId()).orElseThrow();
        ArticleEntity newArticleEntity = ArticleEntity.builder()
                .id(articleDTO.getArticleId())
                .category(articleDTO.getCategory())
                .userEntity(userRepository.findById(articleDTO.getUserPK()).orElseThrow())
                .title(articleDTO.getTitle())
                .content(articleDTO.getContent())
                .views(articleEntity.getViews())
                .shareStatus(articleDTO.getShareStatus())
                .shareRegion(articleDTO.getShareRegion())
                .build();

        ArticleEntity savedArticleEntity = articleRepository.save(newArticleEntity);
        if(savedArticleEntity == null) return false;

        // 태크-게시물 테이블에 기존 태그 delete
        List<ArticleTagEntity> articleTagEntityList = articleTagRepository.findByArticleId(articleDTO.getArticleId());
        for(ArticleTagEntity articleTagEntity : articleTagEntityList){
            articleTagRepository.delete(articleTagEntity);
        }

        // 태크-게시물 테이블에 insert
        for(String tag : articleDTO.getTags()){
            ArticleTagEntity articleTagEntity = ArticleTagEntity.builder()
                    .articleEntity(articleRepository.findById(savedArticleEntity.getId()).orElseThrow())
                    .tagEntity(tagRepository.findByName(tag))
                    .build();

            articleTagRepository.save(articleTagEntity);
        }

        return true;
    }

    @Override
    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }

    @Override
    public Page<ArticleListDTO> readArticleList(String category, Integer page, Integer size) {
        List<ArticleEntity> articleEntities = articleRepository.findAllByCategory(category);
        List<ArticleListDTO> articleListDTOList = new ArrayList<>();
        for(ArticleEntity articleEntity : articleEntities){
            // 이미지 조회
            // 유저 조회
            UserEntity userEntity = userRepository.findById(articleEntity.getId()).orElseThrow();
            // 태그 조회
            List<String> tags = new ArrayList<>();
            List<ArticleTagEntity> articleTagEntityList = articleTagRepository.findByArticleId(articleEntity.getId());
            for(ArticleTagEntity entity : articleTagEntityList){
                tags.add(tagRepository.findById(entity.getTagId()).orElseThrow().getName());
            }
            // 댓글 조회
            List<CommentEntity> commentEntityList = (List<CommentEntity>) commentRepository.findByArticleId(articleEntity.getId());
            // bookmark 여부 조회
            // 복합키 사용을 위한 id 등록
            ArticleBookmarkEntityPK articleBookmarkEntityPK = new ArticleBookmarkEntityPK();
            articleBookmarkEntityPK.setUserEntity(userEntity.getId());
            articleBookmarkEntityPK.setArticleEntity(articleEntity.getId());
            boolean bookmark;
            if(articleBookmarkRepository.findById(articleBookmarkEntityPK).isPresent()){
                bookmark = false;
            }else bookmark = true;
            ArticleListDTO articleListDTO = ArticleListDTO.builder()
                    .articleId(articleEntity.getId())
                    .category(articleEntity.getCategory())
                    .imgs(null)
                    .userPK(articleEntity.getUserPK())
                    .nickName(userEntity.getNickName())
                    .profile(userEntity.getProfile())
                    .title(articleEntity.getTitle())
                    .tags(tags)
                    .views(articleEntity.getViews())
                    .commentCnt(commentEntityList.size())
                    .bookmark(bookmark)
                    .shareRegion(articleEntity.getShareRegion())
                    .shareStatus(articleEntity.getShareStatus())
                    .createTime(articleEntity.getCreatedDate())
                    .updateTime(articleEntity.getLastModifiedDate())
                    .build();

            articleListDTOList.add(articleListDTO);
        }




//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
//        //Page<ArticleListDTO> result = articleRepository.findAllByCategory(category, pageRequest);
//        int start = (int) pageRequest.getOffset();
//        int end = Math.min((start+pageRequest.getPageSize()), articleListDTOList.size());
//        Page<ArticleListDTO> articleListDTOPage = new PageImpl<>(articleListDTOList.subList(start, end), pageRequest, articleListDTOList);
        return null;
    }


}
