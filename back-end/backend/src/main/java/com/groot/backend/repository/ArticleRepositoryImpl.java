package com.groot.backend.repository;

import com.groot.backend.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@RequiredArgsConstructor
@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private QArticleEntity articleEntity;
    @Override
    public List<ArticleEntity> filterRegion(String[] region) {
        articleEntity = QArticleEntity.articleEntity;
        List<ArticleEntity> result = queryFactory
                .selectFrom(articleEntity)
                .where(eqRegions(region))
                .fetch();

        return result;
    }

    @Override
    public List<ArticleEntity> search(String keyword) {
        articleEntity = QArticleEntity.articleEntity;
        List<ArticleEntity> result = queryFactory
                .selectFrom(articleEntity)
                .where(eqTitle(keyword))
                .fetch();

        // 내용 + 제목 검색
//        List<ArticleEntity> result = queryFactory
//                .selectFrom(articleEntity)
//                .where(eqTitle(keyword)
//                        .or(eqContent(keyword)))
//                .fetch();


        return result;
    }

    // 사용자 이름 + 나눔 카테고리 글 조회
    @Override
    public List<ArticleEntity> findUserSharedArticle(Long userPK, Long articleId) {
        articleEntity = QArticleEntity.articleEntity;
        QUserEntity user = QUserEntity.userEntity;
        List<ArticleEntity> result = queryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.userPK.eq(userPK),
                        articleEntity.category.eq("나눔"),
                        articleEntity.id.ne(articleId))
                .fetch();
        return result;
    }




    // 키워드로 게시글 제목 검색
    private BooleanExpression eqTitle(String keyword){
        return keyword == null ? null : articleEntity.title.contains(keyword);
    }

    // 키워드로 게시글 내용 검색
    private BooleanExpression eqContent(String keyword){
        return keyword == null ? null : articleEntity.content.contains(keyword);
    }

    // 필터링 복수 검색
    private BooleanBuilder eqRegions(String[] regions){
        if(regions ==null){
            return null;
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        for(String region : regions){
            booleanBuilder.or(articleEntity.shareRegion.eq(region));
        }
        return booleanBuilder;
    }
}
