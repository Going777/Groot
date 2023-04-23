package com.groot.backend.controller;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.UserDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    // 회원가입
    @PostMapping()
    public ResponseEntity signup(@RequestBody UserDTO userDTO){
        Map<String, Object> resultMap = new HashMap<>();
        if(userService.createUser(userDTO)==null){
            // resultMap.put("accessToken", null);
            resultMap.put("result", FAIL);
            resultMap.put("msg", "회원가입에 실패하였습니다.");

            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "회원가입 되었습니다.");
        return ResponseEntity.ok().body(resultMap);
    }

    // 아이디 중복 확인
    @GetMapping("/userId/{userId}")
    public ResponseEntity IdDuplicateCheck(@PathVariable String userId){
        Map<String, Object> resultMap = new HashMap<>();
        if(userService.isExistedId(userId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "이미 존재하는 아이디입니다.");
           return ResponseEntity.badRequest().body(resultMap);
        }else{
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "사용 가능한 아이디입니다.");
            return ResponseEntity.status(HttpStatus.OK).body(resultMap);
        }
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname/{nickname}")
    public ResponseEntity NickNameDuplicateCheck(@PathVariable String nickname){
        Map<String, Object> resultMap = new HashMap<>();
        if(userService.isExistedNickName(nickname)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "이미 존재하는 닉네임입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }else{
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "사용 가능한 닉네임입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
    }


    // 회원정보 조회

    // 프로필 변경

    // 비밀번호 변경

    // 회원탈퇴

    // 로그인
    @PostMapping("login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO){
        Map<String, Object> resultMap = new HashMap<>();

        // 사용자 존재 여부 확인
        if(!userService.isExistedId(loginDTO.getUserId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
        TokenDTO tokenDTO = userService.login(loginDTO);
        resultMap.put("accessToken", tokenDTO.getAccessToken());
        resultMap.put("result", SUCCESS);
        resultMap.put("msg","로그인 성공");
        return ResponseEntity.ok().body(resultMap);
    }

    // 로그아웃

    // 토큰 갱신

    // 유저 작성글 조회

    // 유저 북마크 조회

    // 유저 식물 조회
}
