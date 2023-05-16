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

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final ListPath<DiaryEntity, QDiaryEntity> diaryEntities = this.<DiaryEntity, QDiaryEntity>createList("diaryEntities", DiaryEntity.class, QDiaryEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> experience = createNumber("experience", Integer.class);

    public final NumberPath<Double> humidity = createNumber("humidity", Double.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> illuminance = createNumber("illuminance", Integer.class);

    public final StringPath imgPath = createString("imgPath");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> nutrientsDate = createDateTime("nutrientsDate", java.time.LocalDateTime.class);

    public final ListPath<PlanEntity, QPlanEntity> planEntities = this.<PlanEntity, QPlanEntity>createList("planEntities", PlanEntity.class, QPlanEntity.class, PathInits.DIRECT2);

    public final QPlantEntity plantEntity;

    public final NumberPath<Long> plantId = createNumber("plantId", Long.class);

    public final StringPath plantKrName = createString("plantKrName");

    public final DateTimePath<java.time.LocalDateTime> pruningDate = createDateTime("pruningDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> saleDate = createDateTime("saleDate", java.time.LocalDateTime.class);

    public final BooleanPath share = createBoolean("share");

    public final BooleanPath survival = createBoolean("survival");

    public final NumberPath<Double> temperature = createNumber("temperature", Double.class);

    public final QUserEntity userEntity;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> waterDate = createDateTime("waterDate", java.time.LocalDateTime.class);

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

