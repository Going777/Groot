package com.groot.backend.repository;

import com.groot.backend.entity.DiaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<DiaryEntity, Long>, DiaryRepositoryCustom {
    Page<DiaryEntity> findAllByPotId(Long potId, PageRequest pageRequest);

    Page<DiaryEntity> findAllByUserPK(Long userPK, PageRequest pageRequest);

    DiaryEntity findTop1ByUserPKAndCreatedDateBetweenOrderByCreatedDateDesc(Long userPK, LocalDateTime startDateTime, LocalDateTime endDateTime);

    DiaryEntity findTop1ByPotIdAndCreatedDateBetweenOrderByCreatedDateDesc(Long potId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
