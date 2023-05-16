package com.groot.backend.repository;

import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.QArticleEntity;
import com.groot.backend.entity.QNotificationEntity;
import com.groot.backend.entity.QUserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class NotificationRepositoryImpl implements NotificationRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    @Override
    public Long updateIsRead(Long notificationId) {
        QNotificationEntity qNotification = QNotificationEntity.notificationEntity;

        long updateCnt = queryFactory.update(qNotification)
                .set(qNotification.isRead, true)
                .where(qNotification.id.eq(notificationId))
                .execute();
        return updateCnt;
    }
}
