package com.groot.backend.controller;

import com.groot.backend.dto.request.FCMRequestDTO;
import com.groot.backend.service.FCMService;
//import com.groot.backend.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notification")
public class FCMController {
    private final FCMService fcmService;

    @PostMapping
    public String sendNotificationByToken(@RequestBody FCMRequestDTO requestDTO){
        return fcmService.sendNotificationByToken(requestDTO);
    }

}
