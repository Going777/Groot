package com.groot.backend.repository;

import com.groot.backend.entity.PotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PotRepository extends JpaRepository<PotEntity, Long>, PotRepositoryCustom {

    public List<PotEntity> findAllByUserId(Long userId);
}
