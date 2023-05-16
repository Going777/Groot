package com.groot.backend.repository;

import com.groot.backend.entity.DiaryCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryCheckRepository extends JpaRepository<DiaryCheckEntity, Long>, DiaryCheckRepositoryCustom  {
}
