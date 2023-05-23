package com.groot.backend.repository;

import com.groot.backend.entity.ChattingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<ChattingEntity, Long> {
    ChattingEntity findByRoomIdAndSenderId(String roomId, Long senderId);

    List<ChattingEntity> findBySenderId(Long senderId);

    void deleteByRoomId(String roomId);

    Boolean existsByRoomId(String roomId);

    List<ChattingEntity> findBySenderIdOrderByCreatedDateDesc(Long senderId);
}
