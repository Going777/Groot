package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = 663650669L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<ArticleEntity, QArticleEntity> articleEntities = this.<ArticleEntity, QArticleEntity>createList("articleEntities", ArticleEntity.class, QArticleEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final ListPath<DiaryEntity, QDiaryEntity> diaryEntities = this.<DiaryEntity, QDiaryEntity>createList("diaryEntities", DiaryEntity.class, QDiaryEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public final ListPath<PotEntity, QPotEntity> potEntities = this.<PotEntity, QPotEntity>createList("potEntities", PotEntity.class, QPotEntity.class, PathInits.DIRECT2);

    public final StringPath profile = createString("profile");

    public final StringPath token = createString("token");

    public final StringPath userId = createString("userId");

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

