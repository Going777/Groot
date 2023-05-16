package com.groot.backend.service;

import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.EmitterRepository;
import com.groot.backend.repository.NotificationRepository;
import com.groot.backend.dto.response.NotificationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService{
    @Autowired
    private final EmitterRepository emitterRepository;
    @Autowired
    private final NotificationRepository notificationRepository;
    private static Long DEFAULT_TIMEOUT   = 60L * 1000L * 60L;

    @Override
    public SseEmitter subscribe(Long userId, String lastEventId) {
        String emitterId = makeTimeIncludeId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + userId + "]");
        log.info("더미 데이터 전송");
        log.info("emitter: "+emitter.toString()+" eventId: "+eventId+" emitterId: "+emitterId);
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userId, emitterId, emitter);
            log.info("미수신한 데이터가 있다");
        }
        log.info("미수신한 데이터가 있는지 확인하고 emitter 리턴");
        return emitter;
    }

    private String makeTimeIncludeId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    @Override
    public void send(UserEntity receiver, String content, String url, String page, Long contentId) {
        NotificationEntity notification = notificationRepository.save(createNotification(receiver, content, url, page, contentId));

        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverId);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationResponseDTO.toDTO(notification, notification.getId()));
                }
        );
    }

    @Override
    public Long readCheck(Long notificationId) {
        Long result = notificationRepository.updateIsRead(notificationId);
        return result;
    }

    @Override
    public Page<NotificationEntity> notificationList(Long userPK, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return notificationRepository.findAllByUserPK(userPK, pageRequest);
    }

    private NotificationEntity createNotification(UserEntity receiver, String content, String url, String page, Long contentId) {
        return NotificationEntity.builder()
                .receiver(receiver)
                .content(content)
                .page(page)
                .contentId(contentId)
                .isRead(false)
                .build();
    }
}
