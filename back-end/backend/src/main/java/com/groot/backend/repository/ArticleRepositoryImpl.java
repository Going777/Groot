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
    @Override
    public Page<ArticleEntity> filterRegion(String[] region, PageRequest pageRequest) {
        QArticleEntity articleEntity = QArticleEntity.articleEntity;
        List<ArticleEntity> articles = queryFactory
                .selectFrom(articleEntity)
                .where(eqRegions(articleEntity, region))
                .orderBy(articleEntity.createdDate.desc())
                .fetch();

        return convertListToPage(articles, pageRequest);
    }

    // 제목 + 내용 + 태그 검색
    @Override
    public Page<ArticleEntity> search(String category, String[] region, String keyword, PageRequest pageRequest, Boolean shareStatus) {
        QArticleEntity qArticle = QArticleEntity.articleEntity;
        QTagEntity tag = QTagEntity.tagEntity;
        QArticleTagEntity articleTag = QArticleTagEntity.articleTagEntity;
        if(shareStatus != null){
            if(shareStatus == false) shareStatus = null;
        }


        List<ArticleEntity> articles = queryFactory
                .selectFrom(qArticle)
                .where(qArticle.category.eq(category)
                        ,eqRegions(qArticle, region)
                        ,eqShareStatus(qArticle,shareStatus))
                .leftJoin(articleTag).on(qArticle.id.eq(articleTag.articleId))
                .leftJoin(tag).on(articleTag.tagId.eq(tag.id))
                .where(tag.name.eq(keyword)
                                .or(qArticle.title.contains(keyword))
                                .or(qArticle.content.contains(keyword))
                )
                .orderBy(qArticle.createdDate.desc())
                .distinct()
                .fetch();

        return convertListToPage(articles, pageRequest);
    }

    // 사용자 이름 + 나눔 카테고리 글 조회
    @Override
    public List<ArticleEntity> findUserSharedArticle(Long userPK, Long articleId) {
        QArticleEntity articleEntity = QArticleEntity.articleEntity;
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
        QArticleEntity articleEntity = QArticleEntity.articleEntity;
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
        QArticleEntity articleEntity = QArticleEntity.articleEntity;
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

    // 나눔 여부로 필터링
    private BooleanExpression eqShareStatus (QArticleEntity articleEntity, Boolean shareStatus){
        return shareStatus == null ? null : articleEntity.shareStatus.eq(shareStatus);
    }

    // 키워드로 게시글 내용 검색
    private BooleanExpression eqContent(QArticleEntity articleEntity, String keyword){
        return keyword == null ? null : articleEntity.content.contains(keyword);
    }

    // 키워드로 게시글 제목 검색
    private BooleanExpression eqTitle(QArticleEntity articleEntity, String keyword){
        return keyword == null ? null : articleEntity.title.contains(keyword);
    }

    // 필터링 복수 검색
    private BooleanBuilder eqRegions(QArticleEntity articleEntity, String[] regions){

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if(regions != null){
            for(String region : regions){
                booleanBuilder.or(articleEntity.shareRegion.eq(region));
            }
        }
        return booleanBuilder;
    }
}
