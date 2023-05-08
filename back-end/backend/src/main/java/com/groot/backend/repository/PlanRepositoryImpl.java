package com.groot.backend.repository;

import com.groot.backend.entity.PlanEntity;
import com.groot.backend.entity.QNotificationEntity;
import com.groot.backend.entity.QPlanEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PlanRepositoryImpl implements PlanRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<PlanEntity> findAllByDateTime(LocalDateTime start, LocalDateTime end) {
        QPlanEntity qPlan = QPlanEntity.planEntity;
//        JPAUpdateClause update = new JPAUpdateClause(entityManager, qNotification);

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end))
                .fetch();
        return plans;
    }

    @Override
    public long updateDoneAndDateTimeByCodeAndPotId(Integer code, Long potId) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.done, true)
                .set(qPlan.dateTime, LocalDateTime.now())
                .where(qPlan.code.eq(code), qPlan.potId.eq(potId))
                .execute();
        return updateCnt;
    }
}
