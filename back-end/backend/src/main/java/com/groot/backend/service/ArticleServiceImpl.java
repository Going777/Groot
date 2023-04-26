package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.entity.ArticleEntity;
import com.groot.backend.entity.ArticleTagEntity;
import com.groot.backend.entity.TagEntity;
import com.groot.backend.repository.ArticleRepository;
import com.groot.backend.repository.ArticleTagRepository;
import com.groot.backend.repository.TagRepository;
import com.groot.backend.repository.UserRepository;
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
                    .articleId(savedArticleEntity.getId())
                    .tagId(savedTagEntity.getId())
                    .build();

            articleTagRepository.save(articleTagEntity);
        }

        return true;
    }


}
