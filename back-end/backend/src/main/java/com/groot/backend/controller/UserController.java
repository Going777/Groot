package com.groot.backend.controller;

import com.groot.backend.dto.request.*;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.service.ArticleService;
import com.groot.backend.service.S3Service;
import com.groot.backend.service.UserService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ArticleService articleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Service s3Service;
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    // 회원가입
    @PostMapping()
    public ResponseEntity signup(@Valid @RequestBody RegisterDTO registerDTO){
        Map<String, Object> resultMap = new HashMap<>();
        // 아이디 중복 체크
        if(userService.isExistedUserId(registerDTO.getUserId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "이미 존재하는 아이디입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 닉네임 중복 체크
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

            return ResponseEntity.internalServerError().body(resultMap);
        }

        // 회원가입 성공 후 로그인
        String firebaseUserPK = String.format("%06d", tokenDTO.getUserPK());    // 6자리 맞추기
        resultMap.put("accessToken", tokenDTO.getAccessToken());
        resultMap.put("refreshToken", tokenDTO.getRefreshToken());
        resultMap.put("userPK", firebaseUserPK);
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
            return ResponseEntity.ok().body(resultMap);
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
            return ResponseEntity.ok().body(resultMap);
        }
    }


    // 회원정보 조회
    @GetMapping()
    public ResponseEntity readUser(HttpServletRequest request){
        Map<String, Object> resultMap = new HashMap<>();
        if(request.getHeader("Authorization") == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
        Long id = jwtTokenProvider.getIdByAccessToken(request);

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
    @PutMapping()
    public ResponseEntity updateProfile(HttpServletRequest request,
                                        @RequestPart(value = "image", required = false) MultipartFile image,
                                        @Valid @RequestPart(value = "userProfileDTO") UserProfileDTO userProfileDTO) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();

        if(request.getHeader("Authorization") == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        Long id = jwtTokenProvider.getIdByAccessToken(request);
        if(id != userProfileDTO.getUserPK()){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resultMap);
        }

        if(!userService.isExistedId(userProfileDTO.getUserPK())){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if(userService.isExistedNickName(userProfileDTO)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "닉네임 중복");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 기존 프로필 사진 삭제
        if(userProfileDTO.getProfile() != null){
            s3Service.delete(userProfileDTO.getProfile());
        }

        // 프로필 사진 수정
        String imgPath = null;
        if(image != null) {
            // 새 프로필 사진 업로드
            imgPath = s3Service.upload(image, "user");
        }

        // 프로필 업데이트
        try{
            userService.updateProfile(userProfileDTO, imgPath);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "회원정보 수정 완료");
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg", "회원정보 수정 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }


    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity updatePassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO){
        Map<String, Object> resultMap = new HashMap<>();
        // 유저 존재 여부
        if(!userService.isExistedId(userPasswordDTO.getUserPK())){
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

        if(request.getHeader("Authorization") == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

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
    public ResponseEntity login(@Valid @RequestBody LoginDTO loginDTO){
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
        resultMap.put("refreshToken", tokenDTO.getRefreshToken());
        resultMap.put("result", SUCCESS);
        resultMap.put("msg","로그인 성공");
        return ResponseEntity.ok().body(resultMap);
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        Map<String, Object> resultMap = new HashMap<>();
        if(request.getHeader("Authorization") == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
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

    // 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity refreshAccessToken(@NotNull @RequestBody TokenDTO tokenDTO){
        Map<String, Object> resultMap = new HashMap<>();
        // refresh 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(tokenDTO.getRefreshToken())) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "유효하지 않은 토큰입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        // accessToken에서 id 뽑아오기
        Long id = jwtTokenProvider.getIdByAccessToken(tokenDTO.getAccessToken());
        if(!userService.isExistedId(id)){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // accessToken 재발급
        // refreshToken 일치 여부 확인
        String refreshToken = tokenDTO.getRefreshToken();
        TokenDTO result = userService.refreshAccessToken(refreshToken, id);

        if(result == null){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "refresh 토큰 불일치.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        resultMap.put("accessToken", result.getAccessToken());
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "토큰 재발급 완료");
        return ResponseEntity.ok().body(resultMap);
    }

    // 유저 작성글 조회
    @GetMapping("/mypage/article")
    public ResponseEntity readUserArticle(HttpServletRequest request,
                                          @RequestParam Integer page,
                                          @RequestParam Integer size){

        Map<String, Object> resultMap = new HashMap<>();

        if(size == 0){
            resultMap.put("result", FAIL);
            resultMap.put("msg","size값은 1 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        Long id = jwtTokenProvider.getIdByAccessToken(request);

        try{
            Page<ArticleListDTO> result = articleService.readUserArticles(id, page, size);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "유저 작성글 조회 성공");
            resultMap.put("articles", result);
            return ResponseEntity.ok().body(resultMap);

        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg", "게시글 목록 조회 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }

    }
    // 유저 북마크 조회
    @GetMapping("/mypage/bookmark")
    public ResponseEntity readUserBookmark(HttpServletRequest request,
                                           @RequestParam Integer page,
                                           @RequestParam Integer size){
        Map<String, Object> resultMap = new HashMap<>();

        if(size == 0){
            resultMap.put("result", FAIL);
            resultMap.put("msg","size값은 1 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        Long id = jwtTokenProvider.getIdByAccessToken(request);

        try{
            Page<ArticleListDTO> result = articleService.readUserBookmarks(id, page, size);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "유저 북마크 조회 성공");
            resultMap.put("articles", result);
            return ResponseEntity.ok().body(resultMap);

        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg", "북마크 목록 조회 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }


    // 소셜 로그인 (카카오, 네이버)
    @PostMapping("/oauth")
    public ResponseEntity socialLogin(@Valid @RequestBody OAuthUserDTO oAuthUserDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        // 닉네임 중복 체크
        if (userService.isExistedNickName(oAuthUserDTO.getNickName())) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "이미 존재하는 닉네임입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if(!(oAuthUserDTO.getOAuthProvider().equals("kakao") ||  oAuthUserDTO.getOAuthProvider().equals("naver"))){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "OAuthProvider는 'kakao' 또는 'naver'를 입력해주세요");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try {
            // 토큰 검사
            userService.checkKakaoToken(oAuthUserDTO.getAccessToken());


            TokenDTO result = userService.OAuthLogin(oAuthUserDTO);
            if(result == null){
                resultMap.put("result", FAIL);
                resultMap.put("msg", "존재하지 않는 사용자, 회원가입을 진행해주세요.");
                return ResponseEntity.badRequest().body(resultMap);
            }
            resultMap.put("accessToken", result.getAccessToken());
            resultMap.put("refreshToken", result.getRefreshToken());
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", oAuthUserDTO.getOAuthProvider()+" 로그인 성공");
            return ResponseEntity.ok().body(resultMap);
        }catch (IOException e) {
            e.printStackTrace();
            resultMap.put("error", e.getMessage());
            resultMap.put("result", FAIL);
            resultMap.put("msg", oAuthUserDTO.getOAuthProvider()+" 로그인 실패");
            return ResponseEntity.internalServerError().body(resultMap);

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("error",e.getMessage());
            resultMap.put("result", FAIL);
            resultMap.put("msg", oAuthUserDTO.getOAuthProvider()+" 로그인 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }
}
