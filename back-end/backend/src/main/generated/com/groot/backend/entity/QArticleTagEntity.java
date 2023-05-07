package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleTagEntity is a Querydsl query type for ArticleTagEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleTagEntity extends EntityPathBase<ArticleTagEntity> {

    private static final long serialVersionUID = -2142412186L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleTagEntity articleTagEntity = new QArticleTagEntity("articleTagEntity");

    public final QArticleEntity articleEntity;

    public final NumberPath<Long> articleId = createNumber("articleId", Long.class);

    public final QTagEntity tagEntity;

    public final NumberPath<Long> tagId = createNumber("tagId", Long.class);

    public QArticleTagEntity(String variable) {
        this(ArticleTagEntity.class, forVariable(variable), INITS);
    }

    public QArticleTagEntity(Path<? extends ArticleTagEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleTagEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleTagEntity(PathMetadata metadata, PathInits inits) {
        this(ArticleTagEntity.class, metadata, inits);
    }

    public QArticleTagEntity(Class<? extends ArticleTagEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.articleEntity = inits.isInitialized("articleEntity") ? new QArticleEntity(forProperty("articleEntity"), inits.get("articleEntity")) : null;
        this.tagEntity = inits.isInitialized("tagEntity") ? new QTagEntity(forProperty("tagEntity")) : null;
    }

}

