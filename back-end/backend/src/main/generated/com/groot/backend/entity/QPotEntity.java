package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPotEntity is a Querydsl query type for PotEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPotEntity extends EntityPathBase<PotEntity> {

    private static final long serialVersionUID = -1090491175L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPotEntity potEntity = new QPotEntity("potEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> characterId = createNumber("characterId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final ListPath<DiaryEntity, QDiaryEntity> diaryEntities = this.<DiaryEntity, QDiaryEntity>createList("diaryEntities", DiaryEntity.class, QDiaryEntity.class, PathInits.DIRECT2);

    public final NumberPath<Double> humidity = createNumber("humidity", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imgPath = createString("imgPath");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public final DatePath<java.sql.Date> nutrientsDate = createDate("nutrientsDate", java.sql.Date.class);

    public final QPlantEntity plantEntity;

    public final NumberPath<Long> plantId = createNumber("plantId", Long.class);

    public final StringPath plantKrName = createString("plantKrName");

    public final DatePath<java.sql.Date> pruningDate = createDate("pruningDate", java.sql.Date.class);

    public final DatePath<java.sql.Date> saleDate = createDate("saleDate", java.sql.Date.class);

    public final BooleanPath share = createBoolean("share");

    public final BooleanPath survival = createBoolean("survival");

    public final NumberPath<Double> temperature = createNumber("temperature", Double.class);

    public final QUserEntity userEntity;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final DatePath<java.sql.Date> waterDate = createDate("waterDate", java.sql.Date.class);

    public QPotEntity(String variable) {
        this(PotEntity.class, forVariable(variable), INITS);
    }

    public QPotEntity(Path<? extends PotEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPotEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPotEntity(PathMetadata metadata, PathInits inits) {
        this(PotEntity.class, metadata, inits);
    }

    public QPotEntity(Class<? extends PotEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.plantEntity = inits.isInitialized("plantEntity") ? new QPlantEntity(forProperty("plantEntity")) : null;
        this.userEntity = inits.isInitialized("userEntity") ? new QUserEntity(forProperty("userEntity")) : null;
    }

}

