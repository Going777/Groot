package com.groot.backend.repository;

import com.groot.backend.entity.PlanEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepositoryCustom {
    List<PlanEntity> findAllByDateTime(LocalDateTime start, LocalDateTime end);

    int updateDoneAndDateTimeByCodeAndPotId(Integer code, Long potId);
}
