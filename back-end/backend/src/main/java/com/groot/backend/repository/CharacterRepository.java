package com.groot.backend.repository;

import com.groot.backend.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CharacterRepository extends JpaRepository<CharacterEntity, Long>{
    @Query(value = "select * from characters where type=:type and level=:level", nativeQuery = true)
    CharacterEntity findByTypeAndLevel(Long type, Integer level);
}