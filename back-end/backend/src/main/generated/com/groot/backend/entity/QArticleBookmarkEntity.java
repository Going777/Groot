package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleBookmarkEntity is a Querydsl query type for ArticleBookmarkEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleBookmarkEntity extends EntityPathBase<ArticleBookmarkEntity> {

    private static final long serialVersionUID = -1797436400L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleBookmarkEntity articleBookmarkEntity = new QArticleBookmarkEntity("articleBookmarkEntity");

    public final QArticleEntity articleEntity;

    public final NumberPath<Long> articleId = createNumber("articleId", Long.class);

    public final QUserEntity userEntity;

    public QArticleBookmarkEntity(String variable) {
        this(ArticleBookmarkEntity.class, forVariable(variable), INITS);
    }

    public QArticleBookmarkEntity(Path<? extends ArticleBookmarkEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleBookmarkEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleBookmarkEntity(PathMetadata metadata, PathInits inits) {
        this(ArticleBookmarkEntity.class, metadata, inits);
    }

    public QArticleBookmarkEntity(Class<? extends ArticleBookmarkEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.articleEntity = inits.isInitialized("articleEntity") ? new QArticleEntity(forProperty("articleEntity"), inits.get("articleEntity")) : null;
        this.userEntity = inits.isInitialized("userEntity") ? new QUserEntity(forProperty("userEntity")) : null;
    }

}

