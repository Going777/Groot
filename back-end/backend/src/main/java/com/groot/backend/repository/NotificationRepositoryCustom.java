package com.groot.backend.repository;

import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;

public interface NotificationRepositoryCustom {
    @Transactional
    @Modifying
    Long updateIsRead(Long notificationId);
}
