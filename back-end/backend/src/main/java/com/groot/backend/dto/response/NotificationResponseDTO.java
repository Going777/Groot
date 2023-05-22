package com.groot.backend.dto.response;

import com.groot.backend.entity.NotificationEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.Collections;

@Builder
@Getter
public class NotificationResponseDTO {
    private Long receiver;

    private String content;

    private String page;

    private Long contentId;

    private boolean isRead;

    private Long id;

    private LocalDateTime createDate;

    private String chattingRoomId;

    public static NotificationResponseDTO toDTO (NotificationEntity notification, Long id){
        NotificationResponseDTO result = NotificationResponseDTO.builder()
                .content(notification.getContent())
                .page(notification.getPage())
                .contentId(notification.getContentId())
                .receiver(notification.getReceiver().getId())
                .isRead(notification.getIsRead())
                .chattingRoomId(notification.getChattingRoomId())
                .id(id)
                .build();
        return result;
    }

    public static Page<NotificationResponseDTO> toPageDTO (Page<NotificationEntity> notificationEntityPage){
        Page<NotificationResponseDTO> dtoList = notificationEntityPage.map(a ->
                NotificationResponseDTO.builder()
                        .id(a.getId())
                        .page(a.getPage())
                        .content(a.getContent())
                        .isRead(a.getIsRead())
                        .contentId(a.getContentId())
                        .receiver(a.getReceiver().getId())
                        .createDate(a.getCreatedDate())
                        .chattingRoomId(a.getChattingRoomId())
                        .build());
        return dtoList;
    }
}
