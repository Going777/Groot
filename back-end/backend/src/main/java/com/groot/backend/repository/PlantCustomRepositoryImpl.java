package com.groot.backend.repository;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.entity.PlantEntity;
import com.groot.backend.entity.QPlantEntity;
import com.groot.backend.util.PlantCodeUtil;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PlantCustomRepositoryImpl implements PlantCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    private final Logger logger = LoggerFactory.getLogger(PlantCustomRepositoryImpl.class);

    private QPlantEntity plantEntity;

    @Override
    public List<PlantEntity> search(PlantSearchDTO plantSearchDTO) throws InvalidRequestStateException {
        plantEntity = QPlantEntity.plantEntity;

        try {
            List<PlantEntity> ret = jpaQueryFactory
                    .selectFrom(plantEntity)
                    .where(eqKrName(plantSearchDTO.getName()),
                            eqLevel(plantSearchDTO.getDifficulty()),
                            eqLightDemand(plantSearchDTO.getLux()),
                            eqGrwType(plantSearchDTO.getGrowth())
                    )
                    .offset(plantSearchDTO.getPage() * 30)
                    .limit(30)
                    .fetch();
            return ret;
        } catch (NullPointerException | IllegalArgumentException e) {
            logger.info("NullPointer Exception : {}", e.getCause());
            throw new InvalidRequestStateException();
        }
    }

    private BooleanExpression eqKrName(String krName) {
        if(krName == null || krName.trim().length() == 0) {
            logger.info("empty string : name");
            return null;
        }
        return plantEntity.krName.contains(krName);
    }

    private BooleanExpression eqLevel(String[] mgmtLevel) {
        if(mgmtLevel == null || mgmtLevel.length == 0){
            return null;
        }
        List<Integer> levels = new ArrayList<>();
        for(int i=0; i<mgmtLevel.length; i++) {
            levels.add(PlantCodeUtil.mgmtLevel.get(mgmtLevel[i].trim()));
        }
        return plantEntity.mgmtLevel.in(levels);
    }

    private BooleanExpression eqLightDemand(String[] demand) {
        if(demand == null || demand.length == 0) {
            return null;
        }
        BooleanExpression ret = null;

        for(int i=0; i<demand.length; i++) {
            int bit = PlantCodeUtil.lightLevel.get(demand[i].trim());
            int target = 1 << bit;
            NumberTemplate numberTemplate = Expressions.numberTemplate(Integer.class, "function('bitand', {0}, {1})",
                    plantEntity.lightDemand, target);

            if (ret == null) {
                ret = numberTemplate.gt(0);
            } else {
                ret = ret.or(numberTemplate.gt(0));
            }
        }
        return ret;
    }

    private BooleanExpression eqGrwType(String[] type) {
        if(type == null || type.length == 0) {
            return null;
        }
        BooleanExpression ret;

        ret = plantEntity.grwType.contains(type[0]);
        if("다육형".equals(type[0])) {
            for(int i = 0; i< PlantCodeUtil.succulents.length; i++) {
                ret = ret.or(plantEntity.grwType.contains(PlantCodeUtil.succulents[i]));
            }
        }
        for(int i=1; i<type.length; i++) {
            logger.info("Add more type : {}", type[i]);
            ret = ret.or(plantEntity.grwType.contains(type[i]));
        }
        return ret;
    }
}
