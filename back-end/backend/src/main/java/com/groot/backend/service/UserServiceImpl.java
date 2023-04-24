package com.groot.backend.service;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.RegisterDTO;
import com.groot.backend.dto.response.UserDTO;
import com.groot.backend.dto.response.TokenDTO;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.UserRepository;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean isExistedId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    @Override
    public boolean isExistedNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
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
                .token(refreshToken)
                .build();

        userRepository.save(newUserEntity);
        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
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
                .build();

        userRepository.save(newUserEntity);
        return TokenDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    @Override
    public boolean deleteUser(Long id) {
        if(!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
}
