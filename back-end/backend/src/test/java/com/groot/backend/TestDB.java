package com.groot.backend;

import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.NotificationRepository;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class TestDB {
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void init(){
        UserEntity user1 = UserEntity.builder()
                .userId("user1")
                .nickName("user1")
                .password(passwordEncoder.encode("password"))
                .build();
        UserEntity user2 = UserEntity.builder()
                .userId("user2")
                .nickName("user2")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
    }

    public UserEntity findGeneralMember(){
        return userRepository.findByUserId("user1");
    }
}
