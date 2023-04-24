package com.groot.backend.controller;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.RegisterDTO;
import com.groot.backend.dto.request.UserPasswordDTO;
import com.groot.backend.dto.response.UserDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.service.UserService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    // 회원가입
    @PostMapping()
    public ResponseEntity signup(@RequestBody RegisterDTO registerDTO){
        Map<String, Object> resultMap = new HashMap<>();
        // 아이디 중복 체크
        if(userService.isExistedUserId(registerDTO.getUserId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "이미 존재하는 아이디입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 이메일 중복 체크
        if(userService.isExistedNickName(registerDTO.getNickName())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "이미 존재하는 닉네임입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 회원가입 실패
        TokenDTO tokenDTO = userService.createUser(registerDTO);
        if(tokenDTO==null){
            // resultMap.put("accessToken", null);
            resultMap.put("result", FAIL);
            resultMap.put("msg", "회원가입에 실패하였습니다.");

            return ResponseEntity.badRequest().body(resultMap);
        }

        // 회원가입 성공 후 로그인
        resultMap.put("accessToken", tokenDTO.getAccessToken());
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "회원가입 되었습니다.");
        return ResponseEntity.ok().body(resultMap);
    }

    // 아이디 중복 확인
    @GetMapping("/userId/{userId}")
    public ResponseEntity IdDuplicateCheck(@PathVariable String userId){
        Map<String, Object> resultMap = new HashMap<>();
        if(userService.isExistedUserId(userId)){
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
    @GetMapping()
    public ResponseEntity readUser(HttpServletRequest request){
        Long id = jwtTokenProvider.getIdByAccessToken(request);

        Map<String, Object> resultMap = new HashMap<>();
        UserEntity userEntity = userService.readUser(id);
        if(userEntity == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }else {
            resultMap.put("user", userEntity.toUserDTO());
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "회원정보 조회 완료");
            return ResponseEntity.ok().body(resultMap);
        }
    }


    // 프로필 변경 (닉네임, 프로필 사진 변경)

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity updatePassword(@RequestBody UserPasswordDTO userPasswordDTO){
        Map<String, Object> resultMap = new HashMap<>();
        // 유저 존재 여부
        if(!userService.isExistedId(userPasswordDTO.getId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 비밀번호 일치 확인
        if(!userService.updatePassword(userPasswordDTO)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "비밀번호 불일치");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 비밀번호 변경 성공
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "비밀번호를 변경하였습니다.");
        return ResponseEntity.ok().body(resultMap);
    }

    // 회원탈퇴
    @DeleteMapping()
    public ResponseEntity deleteUser(HttpServletRequest request){
        Map<String, Object> resultMap = new HashMap<>();
        Long id = jwtTokenProvider.getIdByAccessToken(request);
        if(!userService.deleteUser(id)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 아이디입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "회원탈퇴 되었습니다.");
        return ResponseEntity.ok().body(resultMap);

    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO){
        Map<String, Object> resultMap = new HashMap<>();

        // 사용자 존재 여부 확인
        if(!userService.isExistedUserId(loginDTO.getUserId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        TokenDTO tokenDTO = userService.login(loginDTO);
        // 비밀 번호 불일치
        if(tokenDTO == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "비밀번호 불일치");
            return ResponseEntity.badRequest().body(resultMap);
        }

        resultMap.put("accessToken", tokenDTO.getAccessToken());
        resultMap.put("result", SUCCESS);
        resultMap.put("msg","로그인 성공");
        return ResponseEntity.ok().body(resultMap);
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        Map<String, Object> resultMap = new HashMap<>();
        Long id = jwtTokenProvider.getIdByAccessToken(request);
        if(!userService.logout(id)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "로그아웃 완료");
        return ResponseEntity.ok().body(resultMap);
    }

    // 토큰 갱신

    // 유저 작성글 조회

    // 유저 북마크 조회

    // 유저 식물 조회
}
