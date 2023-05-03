package com.groot.backend.dto.response;

import com.groot.backend.entity.NotificationEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationResponseDTO {
    private Long receiver;

    private String content;

    private String page;

    private Long contentId;

    private boolean isRead;

    private Long notificationId;

    public static NotificationResponseDTO create(NotificationEntity notification, Long id){
        NotificationResponseDTO result = NotificationResponseDTO.builder()
                .content(notification.getContent())
                .page(notification.getPage())
                .contentId(notification.getContentId())
                .receiver(notification.getReceiver().getId())
                .isRead(notification.getIsRead())
                .notificationId(id)
                .build();
        return result;
    }
}
