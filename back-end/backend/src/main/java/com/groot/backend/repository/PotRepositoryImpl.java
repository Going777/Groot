package com.groot.backend.repository;

import com.groot.backend.entity.QNotificationEntity;
import com.groot.backend.entity.QPotEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PotRepositoryImpl implements PotRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public Long updateExpLevelById(Long potId, Integer exp, Integer level) {
        QPotEntity qPot = QPotEntity.potEntity;
//        JPAUpdateClause update = new JPAUpdateClause(entityManager, qNotification);

        long updateCnt = queryFactory.update(qPot)
                .set(qPot.experience, exp)
                .set(qPot.level, level)
                .where(qPot.id.eq(potId))
                .execute();
        return updateCnt;
    }
}
