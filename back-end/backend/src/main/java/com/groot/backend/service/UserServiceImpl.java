package com.groot.backend.service;

import com.groot.backend.dto.request.LoginDTO;
import com.groot.backend.dto.request.RegisterDTO;
import com.groot.backend.dto.request.UserPasswordDTO;
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
        UserEntity userEntity = userRepository.findById(userPasswordDTO.getId()).orElseThrow();

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
                .build();
        userRepository.save(newUserEntity);
        return true;
    }


}
