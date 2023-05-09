package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDiaryEntity is a Querydsl query type for DiaryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDiaryEntity extends EntityPathBase<DiaryEntity> {

    private static final long serialVersionUID = -2086713465L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDiaryEntity diaryEntity = new QDiaryEntity("diaryEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final BooleanPath bug = createBoolean("bug");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final QDiaryCheckEntity diaryCheckEntity;

    public final NumberPath<Long> diaryId = createNumber("diaryId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgPath = createString("imgPath");

    public final BooleanPath isLast = createBoolean("isLast");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final BooleanPath nutrients = createBoolean("nutrients");

    public final QPotEntity potEntity;

    public final NumberPath<Long> potId = createNumber("potId", Long.class);

    public final BooleanPath pruning = createBoolean("pruning");

    public final BooleanPath sun = createBoolean("sun");

    public final QUserEntity userEntity;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final BooleanPath water = createBoolean("water");

    public QDiaryEntity(String variable) {
        this(DiaryEntity.class, forVariable(variable), INITS);
    }

    public QDiaryEntity(Path<? extends DiaryEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDiaryEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDiaryEntity(PathMetadata metadata, PathInits inits) {
        this(DiaryEntity.class, metadata, inits);
    }

    public QDiaryEntity(Class<? extends DiaryEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.diaryCheckEntity = inits.isInitialized("diaryCheckEntity") ? new QDiaryCheckEntity(forProperty("diaryCheckEntity"), inits.get("diaryCheckEntity")) : null;
        this.potEntity = inits.isInitialized("potEntity") ? new QPotEntity(forProperty("potEntity"), inits.get("potEntity")) : null;
        this.userEntity = inits.isInitialized("userEntity") ? new QUserEntity(forProperty("userEntity")) : null;
    }

}

