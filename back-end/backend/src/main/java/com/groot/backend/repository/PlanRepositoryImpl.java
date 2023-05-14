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
    public void deleteAllByCodeAndPotId(Integer code, Long potId) {
        QPlanEntity qPlan = QPlanEntity.planEntity;
        queryFactory.delete(qPlan)
                .where(qPlan.code.eq(code), qPlan.potId.eq(potId), qPlan.done.eq(false))
                .execute();
    }

    @Override
    public List<PlanEntity> findAllByDateTimeAndUserPK(LocalDateTime start, LocalDateTime end, Long userPK) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end), qPlan.userPK.eq(userPK))
                .fetch();
        return plans;
    }

    @Override
    public void deleteByCodeAndPotId(Long potId, Integer code, LocalDateTime time) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        queryFactory.delete(qPlan)
                .where(qPlan.code.eq(code), qPlan.done.eq(true), qPlan.dateTime.eq(time), qPlan.potId.eq(potId))
                .execute();
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

    @Override
    public LocalDateTime findLastDateTimeByDoneAndPotIdAndCode(boolean done, Long potId, Integer code) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        LocalDateTime date = queryFactory.select(qPlan.dateTime.max())
                .from(qPlan)
                .where(qPlan.done.eq(done), qPlan.potId.eq(potId), qPlan.code.eq(code))
                .fetchOne();
        return date;
    }

    @Override
    public long updateDoneById(Long planId, boolean done) {
        QPlanEntity qPlan = QPlanEntity.planEntity;
        long updateCnt = queryFactory.update(qPlan)
                .set(qPlan.done, done)
                .where(qPlan.id.eq(planId))
                .execute();
        return updateCnt;
    }

    @Override
    public PlanEntity existsByCodeAndPotIdAndDateTimeBetween(Integer code, Long potId, LocalDateTime start, LocalDateTime end) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end), qPlan.code.eq(code), qPlan.potId.eq(potId))
                .fetch();
        return plans.get(0);
    }

    @Override
    public List<PlanEntity> findAllByDoneAndDateTimeBetween(boolean done, LocalDateTime start, LocalDateTime end) {
        QPlanEntity qPlan = QPlanEntity.planEntity;

        List<PlanEntity> plans = queryFactory.selectFrom(qPlan)
                .where(qPlan.dateTime.between(start, end), qPlan.done.eq(done))
                .fetch();
        return plans;
    }
}
