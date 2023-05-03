package com.groot.backend.dto.response;

import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
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

    public static NotificationResponseDTO create(NotificationEntity notification){
        NotificationResponseDTO result = NotificationResponseDTO.builder()
                .content(notification.getContent())
                .page(notification.getPage())
                .contentId(notification.getContentId())
                .receiver(notification.getReceiver().getId())
                .isRead(notification.getIsRead())
                .build();
        return result;
    }
}
