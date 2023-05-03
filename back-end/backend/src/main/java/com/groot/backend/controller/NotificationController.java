package com.groot.backend.controller;

import com.groot.backend.dto.response.UserDTO;
import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.service.NotificationService;
import com.groot.backend.util.JwtTokenProvider;
import com.groot.backend.util.SseEmitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

    private final SseEmitters sseEmitters;
    private static String SUCCESS = "success";
    private static String FAIL = "fail";
    public static Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

//    public SseController(SseEmitters sseEmitters){
//        this.sseEmitters = sseEmitters;
//    }
//    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public ResponseEntity connect(){
//        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
//        sseEmitters.add(emitter);
//        try{
//            emitter.send(SseEmitter.event()
//                    .name("connect")
//                    .data("connected"));
//        } catch (IOException e){
//            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok(emitter);
//    }
//
    @PutMapping("/readCheck/{notificationId}")
    public ResponseEntity checkRead(@PathVariable Long notificationId){
        Map resultMap = new HashMap();
        Long result = notificationService.readCheck(notificationId);
        if(result<0){
            resultMap.put("msg", "읽음 표시를 실패했습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("msg", "읽음 표시를 성공했습니다.");
        resultMap.put("result", SUCCESS);
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/list")
    public ResponseEntity readList(@RequestParam Integer page, @RequestParam Integer size, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        Page<NotificationEntity> result = notificationService.notificationList(userId, page, size);
        if(result.isEmpty()){
            resultMap.put("msg", "알림을 조회할 수 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("msg", "알림을 조회를 성공했습니다.");
        resultMap.put("result", SUCCESS);
        resultMap.put("notification", result);
        return ResponseEntity.ok().body(resultMap);
    }

//    @GetMapping(value = "/sub", produces = "text/event-stream")
//    public ResponseEntity subscribe(HttpServletRequest request, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId){
//        Long userPK = JwtTokenProvider.getIdByAccessToken(request);
//        SseEmitter sseEmitter = notificationService.subscribe(userPK);
//        return ResponseEntity.ok().body(sseEmitter);
//    }

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(HttpServletRequest request,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        return notificationService.subscribe(userId, lastEventId);
    }

}
