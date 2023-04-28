package com.groot.backend.service;

import com.groot.backend.controller.NotificationController;
import com.groot.backend.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{
//    private final EmitterRepository emitterRepository;
//    @Override
//    public SseEmitter subscribe(Long userId, String lastEventId) {
//        String emitterId = makeTimeIncludeId(userId);
//        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter());
//        return null;
//    }


    @Override
    public SseEmitter subscribe(Long userId) {
        // 현재 클라이언트를 위한 SseEmitter 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            // 연결!!
            sseEmitter.send(SseEmitter.event().name("connect"));
            log.info("sseEM"+sseEmitter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // user의 pk값을 key값으로 해서 SseEmitter를 저장
        NotificationController.sseEmitterMap.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> NotificationController.sseEmitterMap.remove(userId));
        sseEmitter.onTimeout(() -> NotificationController.sseEmitterMap.remove(userId));
        sseEmitter.onError((e) -> NotificationController.sseEmitterMap.remove(userId));

        return sseEmitter;
    }
/*
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

    }*/
}
