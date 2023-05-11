package com.groot.backend.repository;

import com.groot.backend.entity.DiaryCheckEntity;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.entity.QDiaryCheckEntity;
import com.groot.backend.entity.QDiaryEntity;
import com.querydsl.core.BooleanBuilder;
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
        QDiaryCheckEntity qDiaryCheck = QDiaryCheckEntity.diaryCheckEntity;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = LocalDateTime.of(2023,now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0);
        log.info("month"+now.getMonthValue()+" day"+now.getDayOfMonth());
        DiaryCheckEntity diary = queryFactory.selectFrom(qDiaryCheck)
                .where(qDiaryCheck.potId.eq(potId), qDiaryCheck.createdDate.goe(date))
                .fetchOne();

        return diary;
    }
//    // https://www.inflearn.com/questions/94056/%EA%B0%95%EC%82%AC%EB%8B%98-where-%EB%8B%A4%EC%A4%91-%ED%8C%8C%EB%9D%BC%EB%AF%B8%ED%84%B0%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%8F%99%EC%A0%81-%EC%BF%BC%EB%A6%AC-%EC%82%AC%EC%9A%A9%EC%97%90-%EB%8C%80%ED%95%9C-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4
//    private BooleanBuilder ageEq(Integer age) {
//        if (age == null) {
//            return new BooleanBuilder();
//        } else {
//            return new BooleanBuilder(.age.eq(age));
//        }
//    }

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
        List<DiaryCheckEntity> diary = queryFactory.selectFrom(qDiary)
                .where(qDiary.userPK.eq(userId), qDiary.createdDate.between(start, now))
                .fetch();
        return diary;
    }
}
