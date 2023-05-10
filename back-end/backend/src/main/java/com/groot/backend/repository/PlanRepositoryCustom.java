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
    long updateDoneAndDateTimeByCodeAndPotId(Integer code, Long potId);

    LocalDateTime findLastDateTimeByDoneAndPotIdAndCode(boolean Done, Long PotId, Integer code);
}