package com.groot.backend.repository;

import com.groot.backend.entity.PlantEntity;
import com.groot.backend.entity.PotEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlantRepository extends JpaRepository<PlantEntity, Long>, PlantCustomRepository {

    public List<PlantEntity> findByKrNameContains(String name, Pageable pageable);

    @Query(value = "SELECT * FROM plants WHERE sci_name REGEXP :regex", nativeQuery = true)
    public List<PlantEntity> findBySciNameRegex(@Param("regex") String regex);
}
