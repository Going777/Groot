package com.groot.backend.service;

import com.groot.backend.dto.request.ChatRequestDTO;
import com.groot.backend.dto.response.ChatDetailDTO;
import com.groot.backend.dto.response.ChatResponseDTO;
import com.groot.backend.entity.ChattingEntity;

import java.util.List;

public interface ChattingService {
    boolean insertChatting(ChatRequestDTO chatRequestDTO, Long userId);

    ChatDetailDTO getDetail(Long roomNumber, Long userId);

    List<ChatResponseDTO> getList(Long userId);

    boolean deleteChatting(Long roomNumber, Long userPK);
}
