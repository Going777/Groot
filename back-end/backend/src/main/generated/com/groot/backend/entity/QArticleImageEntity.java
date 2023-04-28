package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleImageEntity is a Querydsl query type for ArticleImageEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleImageEntity extends EntityPathBase<ArticleImageEntity> {

    private static final long serialVersionUID = -698103513L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleImageEntity articleImageEntity = new QArticleImageEntity("articleImageEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final QArticleEntity articleEntity;

    public final NumberPath<Long> articleId = createNumber("articleId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath img = createString("img");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public QArticleImageEntity(String variable) {
        this(ArticleImageEntity.class, forVariable(variable), INITS);
    }

    public QArticleImageEntity(Path<? extends ArticleImageEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleImageEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleImageEntity(PathMetadata metadata, PathInits inits) {
        this(ArticleImageEntity.class, metadata, inits);
    }

    public QArticleImageEntity(Class<? extends ArticleImageEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.articleEntity = inits.isInitialized("articleEntity") ? new QArticleEntity(forProperty("articleEntity"), inits.get("articleEntity")) : null;
    }

}

