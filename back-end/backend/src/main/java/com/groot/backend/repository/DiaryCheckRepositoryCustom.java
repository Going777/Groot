package com.groot.backend.repository;

import com.groot.backend.entity.DiaryCheckEntity;
import com.groot.backend.entity.DiaryEntity;

import java.util.List;

public interface DiaryCheckRepositoryCustom {
    DiaryCheckEntity existsByPotIdCreatedDate(Long userPK);

    List<DiaryCheckEntity> findAllByDate(Long userId);
}
