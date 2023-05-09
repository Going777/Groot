package com.groot.backend.repository;

import com.groot.backend.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<PlanEntity, Long>, PlanRepositoryCustom {

    Boolean existsByDateTimeAndCode(LocalDateTime dateTime, Integer code);
}
