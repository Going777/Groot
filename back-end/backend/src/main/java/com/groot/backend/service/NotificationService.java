package com.groot.backend.service;

import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    SseEmitter subscribe(Long userId, String lastEventId);
    void send(UserEntity receiver, String content, String url, String page, Long contentId) ;

}
