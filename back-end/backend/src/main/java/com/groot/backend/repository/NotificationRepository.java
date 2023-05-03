package com.groot.backend.repository;

import com.groot.backend.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, NotificationRepositoryCustom {
    Page<NotificationEntity> findAllByUserPK(@Param("userPK") Long userPK, PageRequest pageRequest);
}
