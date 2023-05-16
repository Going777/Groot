package com.groot.backend.service;

import com.groot.backend.dto.request.*;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;

import java.io.IOException;
import java.util.Map;

public interface UserService {
    boolean isExistedId(Long id);
    boolean isExistedUserId(String userId);
    boolean isExistedNickName(String nickName);
    boolean isExistedNickName(UserProfileDTO userProfileDTO);
    TokenDTO createUser(RegisterDTO registerDTO);
    UserEntity readUser(Long id);
    TokenDTO login(LoginDTO loginDTO);
    boolean deleteUser(Long id);
    boolean logout(Long id);
    boolean updatePassword(UserPasswordDTO userPasswordDTO);
    TokenDTO refreshAccessToken(String refreshToken, Long id);
    boolean updateProfile(UserProfileDTO userProfileDTO, String imgPath);
    Map<String, Object> OAuthLogin(OAuthUserDTO oAuthUserDTO) throws IOException;
    int checkKakaoToken(String accessToken) throws Exception;
}
