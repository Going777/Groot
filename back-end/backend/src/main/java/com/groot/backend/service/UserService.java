package com.groot.backend.service;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.RegisterDTO;
import com.groot.backend.dto.request.UserPasswordDTO;
import com.groot.backend.dto.request.UserProfileDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;

public interface UserService {
    boolean isExistedId(Long id);
    boolean isExistedUserId(String userId);
    boolean isExistedNickName(String nickName);
    TokenDTO createUser(RegisterDTO registerDTO);
    UserEntity readUser(Long id);
    TokenDTO login(LoginDTO loginDTO);
    boolean deleteUser(Long id);
    boolean logout(Long id);
    boolean updatePassword(UserPasswordDTO userPasswordDTO);
    TokenDTO refreshAccessToken(Long id);
    boolean updateProfile(UserProfileDTO userProfileDTO);
}
