package com.groot.backend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlantEntity is a Querydsl query type for PlantEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlantEntity extends EntityPathBase<PlantEntity> {

    private static final long serialVersionUID = -1288691665L;

    public static final QPlantEntity plantEntity = new QPlantEntity("plantEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Integer> area = createNumber("area", Integer.class);

    public final StringPath botName = createString("botName");

    public final StringPath characteristics = createString("characteristics");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final StringPath flrLang = createString("flrLang");

    public final StringPath grwSeason = createString("grwSeason");

    public final NumberPath<Integer> grwSeasonCd = createNumber("grwSeasonCd", Integer.class);

    public final StringPath grwSpeed = createString("grwSpeed");

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath insectInfo = createString("insectInfo");

    public final StringPath krName = createString("krName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Integer> lightDemand = createNumber("lightDemand", Integer.class);

    public final NumberPath<Integer> maxGrwTemp = createNumber("maxGrwTemp", Integer.class);

    public final NumberPath<Integer> mgmtDemand = createNumber("mgmtDemand", Integer.class);

    public final NumberPath<Integer> mgmtLevel = createNumber("mgmtLevel", Integer.class);

    public final StringPath mgmtTip = createString("mgmtTip");

    public final NumberPath<Integer> minGrwTemp = createNumber("minGrwTemp", Integer.class);

    public final NumberPath<Integer> minHumidity = createNumber("minHumidity", Integer.class);

    public final StringPath place = createString("place");

    public final ListPath<PotEntity, QPotEntity> potEntities = this.<PotEntity, QPotEntity>createList("potEntities", PotEntity.class, QPotEntity.class, PathInits.DIRECT2);

    public final StringPath smellDegree = createString("smellDegree");

    public final StringPath toxicInfo = createString("toxicInfo");

    public final NumberPath<Integer> waterCycle = createNumber("waterCycle", Integer.class);

    public final NumberPath<Integer> winterTemp = createNumber("winterTemp", Integer.class);

    public QPlantEntity(String variable) {
        super(PlantEntity.class, forVariable(variable));
    }

    public QPlantEntity(Path<? extends PlantEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlantEntity(PathMetadata metadata) {
        super(PlantEntity.class, metadata);
    }

}

