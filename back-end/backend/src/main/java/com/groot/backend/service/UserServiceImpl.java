package com.groot.backend.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.*;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.UserRepository;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean isExistedId(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isExistedUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Override
    public boolean isExistedNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    @Override
    public boolean isExistedNickName(UserProfileDTO userProfileDTO) {
        UserEntity userEntity = userRepository.findById(userProfileDTO.getUserPK()).orElseThrow();
        if((userProfileDTO.getNickName()).equals(userEntity.getNickName())){
            return false;
        }
        return userRepository.existsByNickName(userProfileDTO.getNickName());
    }

    @Override
    public TokenDTO createUser(RegisterDTO registerDTO) {
        UserEntity userEntity = UserEntity.builder()
                .userId(registerDTO.getUserId())
                .nickName(registerDTO.getNickName())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .build();

        userRepository.save(userEntity);

        // token 생성
        String accessToken = jwtTokenProvider.createAccessToken(userEntity);
        String refreshToken = jwtTokenProvider.createRefreshToken(userEntity.getId());

        UserEntity newUserEntity = userEntity.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .nickName(userEntity.getNickName())
                .password(userEntity.getPassword())
                .profile(userEntity.getProfile())
                .firebaseToken(registerDTO.getFirebaseToken())
                .token(refreshToken)
                .build();

        userRepository.save(newUserEntity);
        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public UserEntity readUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }


    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        // userEntity find
        UserEntity userEntity = userRepository.findByUserId(loginDTO.getUserId());

        // 일치 확인
        if(!passwordEncoder.matches(loginDTO.getPassword(), userEntity.getPassword())){
            return null;
        }

        // token 생성
        String accessToken = jwtTokenProvider.createAccessToken(userEntity);
        String refreshToken = jwtTokenProvider.createRefreshToken(userEntity.getId());

        UserEntity newUserEntity = userEntity.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .nickName(userEntity.getNickName())
                .password(userEntity.getPassword())
                .profile(userEntity.getProfile())
                .token(refreshToken)
                .firebaseToken(loginDTO.getFirebaseToken())
                .build();

        userRepository.save(newUserEntity);
        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public boolean deleteUser(Long id) {
        if(!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean logout(Long id) {
        if(!userRepository.existsById(id)) return false;
        UserEntity userEntity = userRepository.findById(id).orElseThrow();

        UserEntity newEntity = UserEntity.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .nickName(userEntity.getNickName())
                .password(userEntity.getPassword())
                .profile(userEntity.getProfile())
                .token(null)
                .build();

        userRepository.save(newEntity);
        return true;
    }

    @Override
    public boolean updatePassword(UserPasswordDTO userPasswordDTO) {
        // userEntity find
        UserEntity userEntity = userRepository.findById(userPasswordDTO.getUserPK()).orElseThrow();

        // 비밀번호 일치 확인
        if(!passwordEncoder.matches(userPasswordDTO.getPassword(), userEntity.getPassword())){
            return false;
        }

        // 비밀번호 변경
        UserEntity newUserEntity = UserEntity.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .nickName(userEntity.getNickName())
                .password(passwordEncoder.encode(userPasswordDTO.getNewPassword()))
                .profile(userEntity.getProfile())
                .token(userEntity.getToken())
                .firebaseToken(userEntity.getFirebaseToken())
                .build();
        userRepository.save(newUserEntity);
        return true;
    }

    @Override
    public TokenDTO refreshAccessToken(String refreshToken, Long id) {
        // id로 refreshToken 가져오기
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        String userRefreshToken = userEntity.getToken();

        // refreshToken 일치 여부 확인
        if(!userRefreshToken.equals(refreshToken)){
            return null;
        }

        // accessToken 재발급
        String accessToken = jwtTokenProvider.createAccessToken(userEntity);

        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();

    }

    @Override
    public boolean updateProfile(UserProfileDTO userProfileDTO, String imgPath) {
        UserEntity userEntity = userRepository.findById(userProfileDTO.getUserPK()).orElseThrow();
        UserEntity newUserEntity = UserEntity.builder()
                .id(userEntity.getId())
                .userId(userEntity.getUserId())
                .nickName(userProfileDTO.getNickName())
                .password(userEntity.getPassword())
                .profile(imgPath)
                .token(userEntity.getToken())
                .firebaseToken(userEntity.getFirebaseToken())
                .build();

        UserEntity result = userRepository.save(newUserEntity);
        if(result == null) return false;
        return true;
    }

    @Override
    public TokenDTO kakaoLogin(KaKaoUserDTO kaKaoUserDTO) throws IOException {
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        // accessToken으로 사용자 정보 조회
        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection)  url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer "+ kaKaoUserDTO.getAccessToken());

            // 결과 코드 200이면 성공
            // int responseCode = conn.getResponseCode();

            // response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while((line = br.readLine()) != null){
                result += line;
            }

            // Gson 라이브러리로 JSON 파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String kakaoId = element.getAsJsonObject().get("id").getAsString();
            JsonElement kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            boolean hasProfileImage = kakaoAccount.getAsJsonObject().get("profile_image_needs_agreement").getAsBoolean();
            String profileImage = null;
            boolean isDefaultImage;
            // 이미지 제공 동의 : false
            if(!hasProfileImage){
                isDefaultImage = kakaoAccount.getAsJsonObject().get("profile").getAsJsonObject().get("is_default_image").getAsBoolean();
                // 프로필이 디폴트 이미지가 아니면 사용자 프로필 가져오기
                if(!isDefaultImage) {
                    profileImage = kakaoAccount.getAsJsonObject().get("profile").getAsJsonObject().get("profile_image_url").getAsString();
                }
            }

            br.close();

            // nickName과 회원정보가 둘다 없을면 return null
            if(!userRepository.existsByUserId(kakaoId) && kaKaoUserDTO.getNickName() == null){
                return null;
            }

            // 아이디랑 이메일로 우리 서비스에 회원 가입
                // 회원 정보 없으면 회원가입 진행
            if(!userRepository.existsByUserId(kakaoId)){
                UserEntity newUser = UserEntity.builder()
                        .userId(kakaoId)
                        .password("")
                        .profile(profileImage.equals("") ? null : profileImage)
                        .nickName(kaKaoUserDTO.getNickName())
                        .build();

                userRepository.save(newUser);
            }
                // 있으면 로그인 진행
            UserEntity userEntity = userRepository.findByUserId(kakaoId);

            // token 생성
            String accessToken = jwtTokenProvider.createAccessToken(userEntity);
            String refreshToken = jwtTokenProvider.createRefreshToken(userEntity.getId());

            UserEntity newUserEntity = userEntity.builder()
                    .id(userEntity.getId())
                    .userId(userEntity.getUserId())
                    .nickName(userEntity.getNickName())
                    .password(userEntity.getPassword())
                    .profile(userEntity.getProfile())
                    .firebaseToken(kaKaoUserDTO.getFirebaseToken())
                    .token(refreshToken)
                    .build();

            userRepository.save(newUserEntity);
            return TokenDTO.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
