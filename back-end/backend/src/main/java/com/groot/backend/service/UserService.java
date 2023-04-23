package com.groot.backend.service;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.UserDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;

public interface UserService {
    boolean isExistedId(String userId);
    boolean isExistedNickName(String nickName);
    UserEntity createUser(UserDTO userDTO);
    UserEntity readUser(String userId);
    TokenDTO login(LoginDTO loginDTO);
}
