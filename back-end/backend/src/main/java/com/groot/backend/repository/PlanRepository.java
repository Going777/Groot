package com.groot.backend.repository;

import com.groot.backend.entity.PlanEntity;
import net.bytebuddy.asm.Advice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<PlanEntity, Long>, PlanRepositoryCustom {

    List<PlanEntity> findAllByPotId(Long potId);
    Integer deleteAllByPotId(Long potId);

//    PlanEntity findTop1ByCodeAndDateTimeBetweenOrderByDateTimeDesc(Integer code, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
