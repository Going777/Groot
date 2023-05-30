package com.groot.backend.repository;

import com.groot.backend.entity.PotTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PotTransferRepository extends JpaRepository<PotTransferEntity, Long> {

    List<PotTransferEntity> findByToUserEntityId(Long userPK);
}
