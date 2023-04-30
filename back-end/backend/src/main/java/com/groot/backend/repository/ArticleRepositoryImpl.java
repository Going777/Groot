package com.groot.backend.repository;

import com.groot.backend.entity.ArticleEntity;
import com.groot.backend.entity.QArticleEntity;
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


        return result;
    }

    // 게시글 제목 검색
    private BooleanExpression eqTitle(String keyword){
        return keyword == null ? null : articleEntity.title.contains(keyword);
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
