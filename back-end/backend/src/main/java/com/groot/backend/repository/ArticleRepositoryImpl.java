package com.groot.backend.repository;

import com.groot.backend.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
@RequiredArgsConstructor
@Repository
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private QArticleEntity articleEntity;
    @Override
    public Page<ArticleEntity> filterRegion(String[] region, PageRequest pageRequest) {
        articleEntity = QArticleEntity.articleEntity;
        List<ArticleEntity> articles = queryFactory
                .selectFrom(articleEntity)
                .where(eqRegions(region))
                .orderBy(articleEntity.createdDate.desc())
                .fetch();

        return convertListToPage(articles, pageRequest);
    }

    // 제목 + 내용 + 태그 검색
    @Override
    public Page<ArticleEntity> search(String keyword, PageRequest pageRequest) {
        articleEntity = QArticleEntity.articleEntity;
        QTagEntity tag = QTagEntity.tagEntity;
        QArticleTagEntity articleTag = QArticleTagEntity.articleTagEntity;

        List<ArticleEntity> articles = queryFactory
                .selectFrom(articleEntity)
                .join(articleTag).on(articleEntity.id.eq(articleTag.articleId))
                .join(tag).on(articleTag.tagId.eq(tag.id))
                .where(
                        tag.name.eq(keyword)
                                .or(articleEntity.title.contains(keyword))
                                        .or(articleEntity.content.contains(keyword))

                )
                .orderBy(articleEntity.createdDate.desc())
                .fetch();

        return convertListToPage(articles, pageRequest);
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

    @Override
    public Page<ArticleEntity> findAllByUserPK(Long userPK, PageRequest pageRequest) {
        articleEntity = QArticleEntity.articleEntity;
        List<ArticleEntity> result = queryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.userPK.eq(userPK))
                .orderBy(articleEntity.createdDate.desc())
                .fetch();
        return convertListToPage(result, pageRequest);
    }

    @Override
    public List<Long> findBookmarkByUserPK(Long userPK) {
        QArticleBookmarkEntity bookmark = QArticleBookmarkEntity.articleBookmarkEntity;
        List<Long> bookmarks = queryFactory
                .select(bookmark.articleId)
                .from(bookmark)
                .where(bookmark.userPK.eq(userPK))
                .fetch();

        return bookmarks;
    }

    @Override
    public Page<ArticleEntity> findAllById(List<Long> bookmarkList, PageRequest pageRequest) {
        articleEntity = QArticleEntity.articleEntity;
        List<ArticleEntity> result = queryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.id.in(bookmarkList))
                .orderBy(articleEntity.createdDate.desc())
                .fetch();

        return convertListToPage(result, pageRequest);
    }

    // List를 Page로 변환
    public Page<ArticleEntity> convertListToPage(List<ArticleEntity> articles, PageRequest pageRequest){
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start+pageRequest.getPageSize()), articles.size());
        if(start > end){
            return null;
        }
        return new PageImpl<>(articles.subList(start, end), pageRequest, articles.size());
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
