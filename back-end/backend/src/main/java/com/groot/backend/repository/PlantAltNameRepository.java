package com.groot.backend.repository;

import com.groot.backend.entity.PlantAltNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantAltNameRepository extends JpaRepository<PlantAltNameEntity, Long> {

    public List<PlantAltNameEntity> findAllByAltNameContaining(String name);
}
