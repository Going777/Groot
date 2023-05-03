package com.groot.backend.util;

import com.groot.backend.dto.response.NotificationResponseDTO;
import com.groot.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

//    @TransactionalEventListener
//    @Async
//    public void handleNotification(NotificationResponseDTO notificationDTO){
//        notificationService.send(notificationDTO.getReceiver()., notificationDTO.getContent(), notificationDTO.getUrl());
//    }

}
