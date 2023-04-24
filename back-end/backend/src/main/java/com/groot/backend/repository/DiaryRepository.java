package com.groot.backend.repository;

import com.groot.backend.entity.DiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {

}
