package com.groot.backend.repository;

import com.groot.backend.entity.DiaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {
    Page<DiaryEntity> findAllByPotId(Long potId, PageRequest pageRequest);

    Page<DiaryEntity> findAllByUserId(Long userId, PageRequest pageRequest);
    @Query(value = "select * from diary where :now<=created_date", nativeQuery = true)
    List<DiaryEntity> findAllByDate(String now);
}
