package com.groot.backend.repository;

import com.groot.backend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUserId(String userId);
    boolean existsByNickName(String nickName);
    UserEntity findByUserId(String userId);
}
