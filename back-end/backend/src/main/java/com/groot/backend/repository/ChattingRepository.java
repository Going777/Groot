package com.groot.backend.repository;

import com.groot.backend.entity.ChattingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<ChattingEntity, Long> {
    ChattingEntity findByRoomIdAndSenderId(Long roomId, Long senderId);

    List<ChattingEntity> findBySenderId(Long senderId);

    void deleteByRoomIdAndSenderId(Long roomId, Long senderId);

    Boolean existsByRoomIdAndSenderId(Long roomId, Long senderId);
}
