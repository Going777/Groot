package com.groot.backend.repository;

import com.groot.backend.entity.NotificationEntity;

public interface NotificationRepositoryCustom {
    Long updateIsRead(Long notificationId);
}
