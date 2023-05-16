package com.groot.backend.repository;

import com.groot.backend.entity.QDiaryEntity;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor
@Repository
@Slf4j
public class DiaryRepositoryImpl implements DiaryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override   // 해당일 isPotLast가 true였던 것을 false로
    public Long updateIsLastByPotId(Long potId, LocalDateTime now) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        LocalDateTime today = LocalDateTime.of(LocalDate.from(now), LocalTime.of(0, 0, 0));
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isPotLast, false)
                .where(qDiary.potId.eq(potId), qDiary.isPotLast.eq(true), qDiary.createdDate.between(today, now))
                .execute();

        return updateCnt;
    }

    @Override   // 오늘 isUserLast가 true였던 것 false로
    public Long updateIsLastByUserId(Long userId, LocalDateTime now) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        LocalDateTime today = LocalDateTime.of(LocalDate.from(now), LocalTime.of(0, 0, 0));
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isUserLast, false)
                .where(qDiary.userPK.eq(userId), qDiary.isUserLast.eq(true), qDiary.createdDate.between(today, now))
                .execute();

        return updateCnt;
    }

    @Override   // 해당 id 다이어리의 isUserLast 수정
    public Long updateIsUserLastById(Long id, Boolean setUserLast) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isUserLast, setUserLast)
                .where(qDiary.id.eq(id))
                .execute();

        return updateCnt;
    }

    @Override   // 해당 id 다이어리의 isPotLast 수정
    public Long updateIsPotLastById(Long id, Boolean setPotLast) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isPotLast, setPotLast)
                .where(qDiary.id.eq(id))
                .execute();

        return updateCnt;
    }

    @Override
    public Long updateIsPotLastToTrueByPotIdAndDateTime(Long potId, LocalDateTime dateTime) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        LocalDateTime start = LocalDateTime.of(LocalDate.from(dateTime), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.from(dateTime), LocalTime.of(23, 59, 59));

        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isPotLast, true)
                .where(qDiary.potId.eq(potId), qDiary.createdDate.eq(
                        JPAExpressions
                                .select(qDiary.createdDate.max())
                                .from(qDiary)
                                .where(qDiary.createdDate.between(start, end))
                ))
                .execute();

        return updateCnt;
    }

    @Override
    public Long updateIsPotLastToTrueByUserIdAndDateTime(Long userId, LocalDateTime dateTime) {
        QDiaryEntity qDiary = QDiaryEntity.diaryEntity;
        LocalDateTime start = LocalDateTime.of(LocalDate.from(dateTime), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.from(dateTime), LocalTime.of(23, 59, 59));

        Long updateCnt = queryFactory.update(qDiary)
                .set(qDiary.isPotLast, true)
                .where(qDiary.userPK.eq(userId), qDiary.createdDate.eq(
                        JPAExpressions
                                .select(qDiary.createdDate.max())
                                .from(qDiary)
                                .where(qDiary.createdDate.between(start, end))
                ))
                .execute();

        return updateCnt;
    }


}
