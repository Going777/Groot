package com.groot.backend.service;

import com.groot.backend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final EmitterRepository emitterRepository;
    @Override
    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter());
        return null;
    }

    @Override
    public String makeTimeIncludeId(Long userId) {
        return null;
    }

    @Override
    public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {

    }

    @Override
    public boolean hasLostData(String lastEventId) {
        return false;
    }

    @Override
    public void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {

    }
}
