package com.groot.backend.repository;

import com.groot.backend.entity.DiaryCheckEntity;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.entity.QDiaryCheckEntity;
import com.groot.backend.entity.QDiaryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class DiaryCheckRepositoryImpl implements DiaryCheckRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static int[] monthDate = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Override
    public DiaryCheckEntity existsByPotIdCreatedDate(Long potId) {
        QDiaryCheckEntity qDiary = QDiaryCheckEntity.diaryCheckEntity;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = LocalDateTime.of(2023,now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0);
        log.info("month"+now.getMonthValue()+" day"+now.getDayOfMonth());
        DiaryCheckEntity diary = queryFactory.selectFrom(qDiary)
                .where(qDiary.potId.eq(potId), qDiary.createdDate.gt(date))
                .fetchOne();

        return diary;
    }

    @Override
    public List<DiaryCheckEntity> findAllByDate(Long userId) {
        QDiaryCheckEntity qDiary = QDiaryCheckEntity.diaryCheckEntity;
        LocalDateTime now = LocalDateTime.now();
        int day = now.getDayOfMonth() - 7;
        int month = now.getMonthValue();
        if(day < 0){
            month -=1;
            if(month<0) month = 12;
            day = monthDate[month]-day;
        }
        LocalDateTime start = LocalDateTime.of(now.getYear(), month, day, 0, 0, 0);
//        LocalDateTime end = LocalDateTime.of(now);
        log.info("month"+now.getMonthValue()+" day"+now.getDayOfMonth());
        DiaryCheckEntity diary = queryFactory.selectFrom(qDiary)
                .where(qDiary.userPK.eq(userId), qDiary.createdDate.between(start, now))
                .fetchOne();
        return null;
    }
}
