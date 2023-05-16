package com.groot.backend.repository;

import com.groot.backend.entity.PlanEntity;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepositoryCustom {

    @Transactional
    @Modifying
    void deleteAllByCodeAndPotId(Integer code, Long potId);

    List<PlanEntity> findAllByDateTimeAndUserPK(LocalDateTime start, LocalDateTime end, Long userPK);

    @Transactional
    @Modifying
    long updateByCodeAndPotId(Long potId, Integer code, LocalDateTime time);

    @Transactional
    @Modifying
    long updateByCodeAndDiaryId(Integer code, Long diaryId);

    @Transactional
    @Modifying
    long updateDoneAndDateTimeByCodeAndPotId(Integer code, Long potId);

    LocalDateTime findLastDateTimeByDoneAndPotIdAndCode(boolean Done, Long PotId, Integer code);

    @Transactional
    @Modifying
    long updateDoneById(Long planId, boolean done);

    PlanEntity existsByCodeAndPotIdAndDateTimeBetween(Integer code, Long potId, LocalDateTime start, LocalDateTime end);

    List<PlanEntity> findAllByDoneAndDateTimeBetween(boolean done, LocalDateTime start, LocalDateTime end);

    @Transactional
    @Modifying  // 해야할 날짜가 지났지만 done이 0인 애들 날짜 업데이트 // 수정해야함
    long updateDateTimeByDoneAndDateTimeBetween(boolean done, LocalDateTime start, LocalDateTime end);
}
