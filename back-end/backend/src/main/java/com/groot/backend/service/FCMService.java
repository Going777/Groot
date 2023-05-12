package com.groot.backend.service;

import com.groot.backend.dto.request.FCMRequestDTO;

public interface FCMService {
    String sendNotificationByToken(FCMRequestDTO requestDTO);
}
