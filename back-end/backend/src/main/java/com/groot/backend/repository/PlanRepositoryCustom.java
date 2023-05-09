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

    List<PlanEntity> findAllByDateTime(LocalDateTime start, LocalDateTime end);

    @Transactional
    @Modifying
    long updateDoneAndDateTimeByCodeAndPotId(Integer code, Long potId);
}
