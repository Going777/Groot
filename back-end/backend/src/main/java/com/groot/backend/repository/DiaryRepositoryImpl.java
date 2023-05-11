package com.groot.backend.repository;

import com.groot.backend.entity.QDiaryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Repository
@Slf4j
public class DiaryRepositoryImpl implements DiaryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Long updateIsLastByPotId(Long potId, LocalDateTime now) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0);
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isPotLast, false)
                .where(qDiary.potId.eq(potId), qDiary.isPotLast.eq(true), qDiary.createdDate.goe(today))
                .execute();

        return updateCnt;
    }

    @Override
    public Long updateIsLastByUserId(Long userId, LocalDateTime now) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0);
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isUserLast, false)
                .where(qDiary.userPK.eq(userId), qDiary.isUserLast.eq(true), qDiary.createdDate.goe(today))
                .execute();

        return updateCnt;
    }

    @Override
    public Long updateIsUserLastById(Long id, Boolean setUserLast) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isUserLast, setUserLast)
                .where(qDiary.id.eq(id))
                .execute();

        return updateCnt;
    }

    @Override
    public Long updateIsPotLastById(Long id, Boolean setPotLast) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isPotLast, setPotLast)
                .where(qDiary.id.eq(id))
                .execute();

        return updateCnt;
    }
}
