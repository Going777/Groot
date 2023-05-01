package com.groot.backend.service;

import com.groot.backend.controller.NotificationController;
import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.EmitterRepository;
import com.groot.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{
    private final EmitterRepository emitterRepository;

    private final NotificationRepository notificationRepository;
    private static Long DEFAULT_TIMEOUT   = 60L * 1000L * 60L;
    @Override
    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT ));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }


//    @Override
//    public SseEmitter subscribe(Long userId) {
//        // 현재 클라이언트를 위한 SseEmitter 생성
//        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
//        try {
//            // 연결!!
//            sseEmitter.send(SseEmitter.event().name("connect"));
//            log.info("sseEM"+sseEmitter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // user의 pk값을 key값으로 해서 SseEmitter를 저장
//        NotificationController.sseEmitterMap.put(userId, sseEmitter);
//
//        sseEmitter.onCompletion(() -> NotificationController.sseEmitterMap.remove(userId));
//        sseEmitter.onTimeout(() -> NotificationController.sseEmitterMap.remove(userId));
//        sseEmitter.onError((e) -> NotificationController.sseEmitterMap.remove(userId));
//
//        return sseEmitter;
//    }

    @Override
    public String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    @Override
    public void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    @Override
    public boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    @Override
    public void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    @Override
    public void send(UserEntity receiver, String content, String url) {
        NotificationEntity notification = notificationRepository.save(createNotification(receiver, content, url));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDTO.create(notification));
                }
        );
    }

    @Override
    private NotificationEntity createNotification(UserEntity receiver, String content, String url) {
        return NotificationEntity.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }
}
