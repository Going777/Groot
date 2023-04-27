package com.groot.backend.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(Long userId, String lastEventId);
    String makeTimeIncludeId(Long userId);
    void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data);
    boolean hasLostData(String lastEventId);
    void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter);
}
