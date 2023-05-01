package com.groot.backend.service;

import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(Long userId, String lastEventId);
    String makeTimeIncludeId(Long userId);
    void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data);
    boolean hasLostData(String lastEventId);
    void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter);
    void send(UserEntity receiver, String content, String url) ;
    NotificationEntity createNotification(UserEntity receiver, String content, String url)
}
