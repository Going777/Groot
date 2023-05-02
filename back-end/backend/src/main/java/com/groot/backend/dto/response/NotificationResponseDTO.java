package com.groot.backend.dto.response;

import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationResponseDTO {
    private UserEntity receiver;

    private String content;

    private String url;

    private boolean isRead;

    public static NotificationResponseDTO create(NotificationEntity notification){
        NotificationResponseDTO result = NotificationResponseDTO.builder()
                .content(notification.getContent())
                .url(notification.getUrl())
                .receiver(notification.getReceiver())
                .isRead(notification.getIsRead())
                .build();
        return result;
    }
}
