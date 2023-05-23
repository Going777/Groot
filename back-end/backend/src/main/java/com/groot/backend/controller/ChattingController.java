package com.groot.backend.controller;

import com.amazonaws.Response;
import com.google.firebase.auth.FirebaseAuthException;
import com.groot.backend.dto.request.ChatRequestDTO;
import com.groot.backend.dto.response.ChatDetailDTO;
import com.groot.backend.dto.response.ChatResponseDTO;
import com.groot.backend.service.ChattingService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/chattings")
@Slf4j
@RequiredArgsConstructor
public class ChattingController {

    private final ChattingService chattingService;
    private static String SUCCESS = "success";
    private static String FAIL = "fail";

    // 채팅 시작 시 채팅방 저장
    @PostMapping
    public ResponseEntity insertChatting(@RequestBody ChatRequestDTO chatRequestDTO, HttpServletRequest request) throws FirebaseAuthException {
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(!chattingService.insertChatting(chatRequestDTO, userId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "채팅방 저장을 실패했습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "채팅방 저장을 성공했습니다.");
        return ResponseEntity.ok().body(resultMap);
    }

    // 채팅방 클릭 시 상대방 정보 가져오기
    @GetMapping("/detail/{roomId}")
    public ResponseEntity getChattingDetail(@PathVariable String roomId, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        ChatDetailDTO result = chattingService.getDetail(roomId, userId);
        if(result==null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "채팅방 정보 불러오기를 실패했습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "채팅방 정보 불러오기를 성공했습니다.");
        resultMap.put("chatting", result);
        return ResponseEntity.ok().body(resultMap);
    }

    // 채팅방 리스트 주기
    @GetMapping("/list")
    public ResponseEntity getChattingList(HttpServletRequest request) throws ExecutionException, InterruptedException {
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        List<ChatResponseDTO> result = chattingService.getList(userId);
//        if(result.isEmpty()){
//            resultMap.put("result", FAIL);
//            resultMap.put("msg", "채팅방 리스트 불러오기를 실패했습니다.");
//            return ResponseEntity.badRequest().body(resultMap);
//        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "채팅방 리스트 불러오기를 성공했습니다.");
        resultMap.put("chatting", result);
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity deleteChatting(@PathVariable String roomId){
        Map resultMap = new HashMap<>();
        if(!chattingService.deleteChatting(roomId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "채팅방 나가기를 실패했습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "채팅방 나가기를 성공했습니다.");
        return ResponseEntity.ok().body(resultMap);
    }
}
