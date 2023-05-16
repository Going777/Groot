package com.groot.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.ChatRequestDTO;
import com.groot.backend.dto.response.ChatDetailDTO;
import com.groot.backend.dto.response.ChatResponseDTO;
import com.groot.backend.entity.ChattingEntity;
import com.groot.backend.entity.ChattingEntityPK;
import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.ChattingRepository;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChattingServiceImpl implements ChattingService{

    private final ChattingRepository chattingRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public boolean insertChatting(ChatRequestDTO chatRequestDTO, Long userId) {
        UserEntity user1 = userRepository.findById(userId).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        UserEntity user2 = userRepository.findById(chatRequestDTO.getUserPK()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        ChattingEntity chatting1 = ChattingEntity.builder()
                .sender(user1)
                .receiver(user2)
                .roomId(chatRequestDTO.getRoomId())
                .build();
        ChattingEntity chatting2 = ChattingEntity.builder()
                .sender(user2)
                .receiver(user1)
                .roomId(chatRequestDTO.getRoomId())
                .build();
        if(chattingRepository.save(chatting1)==null || chattingRepository.save(chatting2)==null) return false;

        // 채팅방 생성 알림
        String title = "채팅 알림";
        String body = user1.getNickName()+"님이 나눔 채팅을 시작하였습니다.";

        Optional<UserEntity> user = userRepository.findById(user2.getId());
        NotificationEntity noti = NotificationEntity.builder()
                .contentId(chatRequestDTO.getRoomId())
                .page("chatting")
                .isRead(false)
                .content(body)
                .title(title)
                .receiver(user.get())
                .build();

        if(user.isPresent() && user.get().getUserAlarmEntity().getChattingAlarm()) {
            if (user.get().getFirebaseToken() != null) {
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                com.google.firebase.messaging.Message message = Message.builder()
                        .setToken(user.get().getFirebaseToken())
                        .setNotification(notification)
                        .build();

                try {
                    firebaseMessaging.send(message);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public ChatDetailDTO getDetail(Long roomId, Long userId) {
        ChattingEntity chatting = chattingRepository.findByRoomIdAndSenderId(roomId, userId);
        UserEntity user = userRepository.findById(chatting.getReceiverId()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        ChattingEntity receiverChatting = chattingRepository.findByRoomIdAndSenderId(roomId, user.getId());

        ChatDetailDTO chattingDTO = ChatDetailDTO.builder()
                .userPK(user.getId())
                .nickName(user.getNickName())
                .profile(user.getProfile())
                .receive(receiverChatting!=null)
                .build();
        return chattingDTO;
    }

    @Override
    public List<ChatResponseDTO> getList(Long userId) {
        List<ChattingEntity> entityList = chattingRepository.findBySenderId(userId);
        List<ChatResponseDTO> dtoList = new ArrayList<>();
        for(ChattingEntity chattingEntity: entityList){
            UserEntity receiver = userRepository.findById(chattingEntity.getReceiverId()).orElseThrow();
            ChatResponseDTO dto = ChatResponseDTO.builder()
                    .userPK(receiver.getId())
                    .nickName(receiver.getNickName())
                    .profile(receiver.getProfile())
                    .roomId(chattingEntity.getRoomId())
                    .build();
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    @Transactional
    public boolean deleteChatting(Long roomId, Long userPK) {
        if(!chattingRepository.existsByRoomIdAndSenderId(roomId, userPK)){
            return false;
        }
        chattingRepository.deleteByRoomIdAndSenderId(roomId, userPK);
        return true;
    }


}
