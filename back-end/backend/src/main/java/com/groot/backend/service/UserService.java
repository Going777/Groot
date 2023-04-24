package com.groot.backend.service;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.RegisterDTO;
import com.groot.backend.dto.response.UserDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;

public interface UserService {
    boolean isExistedId(String userId);
    boolean isExistedNickName(String nickName);
    TokenDTO createUser(RegisterDTO registerDTO);
    UserEntity readUser(Long id);
    TokenDTO login(LoginDTO loginDTO);
}
