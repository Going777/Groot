package com.groot.backend.repository;

import com.groot.backend.entity.PlantEntity;
import com.groot.backend.entity.PotEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository extends JpaRepository<PlantEntity, Long> {

    public List<PlantEntity> findByKrNameContains(String name, Pageable pageable);

    public PlantEntity findBySciNameStartsWith(String sciName);
}
