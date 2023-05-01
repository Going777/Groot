package com.groot.backend.controller;

import com.groot.backend.service.NotificationService;
import com.groot.backend.util.JwtTokenProvider;
import com.groot.backend.util.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitters sseEmitters;

    public static Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

//    public SseController(SseEmitters sseEmitters){
//        this.sseEmitters = sseEmitters;
//    }
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity connect(){
        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
        sseEmitters.add(emitter);
        try{
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(emitter);
    }

    @PostMapping("/count")
    public ResponseEntity count(){
        sseEmitters.count();
        return ResponseEntity.ok().build();
    }

//    @GetMapping(value = "/sub", produces = "text/event-stream")
//    public ResponseEntity subscribe(HttpServletRequest request, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
//        Long userPK = JwtTokenProvider.getIdByAccessToken(request);
//        SseEmitter sseEmitter = notificationService.subscribe(userPK);
//        return ResponseEntity.ok().body(sseEmitter);
//    }

}
