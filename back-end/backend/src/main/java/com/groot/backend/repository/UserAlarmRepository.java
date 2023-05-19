package com.groot.backend.repository;

import com.groot.backend.entity.UserAlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAlarmRepository extends JpaRepository<UserAlarmEntity, Long> {

}
