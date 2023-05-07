package com.groot.backend.repository;

import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface PotRepositoryCustom {
    @Transactional
    @Modifying
    Long updateExpLevelById(Long potId, Integer exp, Integer level);
}
