package com.groot.backend.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.groot.backend.dto.request.ChatRequestDTO;
import com.groot.backend.dto.response.ChatDetailDTO;
import com.groot.backend.dto.response.ChatResponseDTO;
import com.groot.backend.entity.ChattingEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ChattingService {
    boolean insertChatting(ChatRequestDTO chatRequestDTO, Long userId) throws FirebaseAuthException;

    ChatDetailDTO getDetail(String roomNumber, Long userId);

    List<ChatResponseDTO> getList(Long userId) throws ExecutionException, InterruptedException;

    boolean deleteChatting(String roomNumber);
}
