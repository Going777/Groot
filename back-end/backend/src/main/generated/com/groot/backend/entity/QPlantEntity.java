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

    public final StringPath characteristics = createString("characteristics");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final StringPath grwSeason = createString("grwSeason");

    public final StringPath grwSpeed = createString("grwSpeed");

    public final StringPath grwType = createString("grwType");

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath img = createString("img");

    public final StringPath insectInfo = createString("insectInfo");

    public final StringPath krName = createString("krName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Integer> lightDemand = createNumber("lightDemand", Integer.class);

    public final NumberPath<Integer> maxGrwTemp = createNumber("maxGrwTemp", Integer.class);

    public final NumberPath<Integer> maxHumidity = createNumber("maxHumidity", Integer.class);

    public final StringPath mgmtDemand = createString("mgmtDemand");

    public final NumberPath<Integer> mgmtLevel = createNumber("mgmtLevel", Integer.class);

    public final StringPath mgmtTip = createString("mgmtTip");

    public final NumberPath<Integer> minGrwTemp = createNumber("minGrwTemp", Integer.class);

    public final NumberPath<Integer> minHumidity = createNumber("minHumidity", Integer.class);

    public final StringPath place = createString("place");

    public final ListPath<PotEntity, QPotEntity> potEntities = this.<PotEntity, QPotEntity>createList("potEntities", PotEntity.class, QPotEntity.class, PathInits.DIRECT2);

    public final StringPath sciName = createString("sciName");

    public final NumberPath<Integer> smellDegree = createNumber("smellDegree", Integer.class);

    public final StringPath toxicInfo = createString("toxicInfo");

    public final NumberPath<Integer> waterCycle = createNumber("waterCycle", Integer.class);

    public final NumberPath<Integer> winterMinTemp = createNumber("winterMinTemp", Integer.class);

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

