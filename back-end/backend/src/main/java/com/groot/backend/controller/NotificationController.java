package com.groot.backend.controller;

import com.groot.backend.dto.response.NotificationResponseDTO;
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

    private static String SUCCESS = "success";
    private static String FAIL = "fail";
    public static Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    @PutMapping("/readCheck/{notificationId}/{userPK}")
    public ResponseEntity checkRead(@PathVariable Long notificationId, @PathVariable Long userPK, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(!userId.equals(userPK)){
            resultMap.put("msg", "수정 권한이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        Long result = notificationService.readCheck(notificationId);
        if(result<0){
            resultMap.put("msg", "읽음 표시를 실패했습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.internalServerError().body(resultMap);
        }
        resultMap.put("msg", "읽음 표시를 성공했습니다.");
        resultMap.put("result", SUCCESS);
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/list")
    public ResponseEntity readList(@RequestParam Integer page, @RequestParam Integer size, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        Page<NotificationEntity> resultEntity = notificationService.notificationList(userId, page, size);
        Page<NotificationResponseDTO> result = NotificationResponseDTO.toPageDTO(resultEntity);
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


    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(HttpServletRequest request,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        log.info("sse 연결 요청");
        SseEmitter result =  notificationService.subscribe(userId, lastEventId);
        if(result==null){
            log.info("result가 널이다");
        }
        log.info("연결 결과 sseEmitter 전송: "+result.toString());
        return result;
    }

}
