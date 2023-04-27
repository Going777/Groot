package com.groot.backend.repository;

import com.groot.backend.entity.PotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PotRepository extends JpaRepository<PotEntity, Long> {
}
