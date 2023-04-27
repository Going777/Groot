package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleEntity is a Querydsl query type for ArticleEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleEntity extends EntityPathBase<ArticleEntity> {

    private static final long serialVersionUID = -227786182L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleEntity articleEntity = new QArticleEntity("articleEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<ArticleImageEntity, QArticleImageEntity> articleImageEntityList = this.<ArticleImageEntity, QArticleImageEntity>createList("articleImageEntityList", ArticleImageEntity.class, QArticleImageEntity.class, PathInits.DIRECT2);

    public final StringPath category = createString("category");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath shareRegion = createString("shareRegion");

    public final BooleanPath shareStatus = createBoolean("shareStatus");

    public final StringPath title = createString("title");

    public final QUserEntity userEntity;

    public final NumberPath<Long> userPK = createNumber("userPK", Long.class);

    public final NumberPath<Long> views = createNumber("views", Long.class);

    public QArticleEntity(String variable) {
        this(ArticleEntity.class, forVariable(variable), INITS);
    }

    public QArticleEntity(Path<? extends ArticleEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleEntity(PathMetadata metadata, PathInits inits) {
        this(ArticleEntity.class, metadata, inits);
    }

    public QArticleEntity(Class<? extends ArticleEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userEntity = inits.isInitialized("userEntity") ? new QUserEntity(forProperty("userEntity")) : null;
    }

}

