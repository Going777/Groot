package com.groot.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.groot.backend.dto.request.FCMRequestDTO;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FCMServiceImpl implements FCMService{

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/groot-e4de7/messages:send";
    private final ObjectMapper objectMapper;
    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    @Override
    public String sendNotificationByToken(FCMRequestDTO requestDTO) {
        Optional<UserEntity> user = userRepository.findById(requestDTO.getTargetUserId());
        if(user.isPresent()){
            if(user.get().getFirebaseToken() != null){
                Notification notification = Notification.builder()
                        .setTitle(requestDTO.getTitle())
                        .setBody(requestDTO.getBody())
                        .build();

                Message message = Message.builder()
                        .setToken(user.get().getFirebaseToken())
                        .setNotification(notification)
                        .build();

                try{
                    firebaseMessaging.send(message);
                    return "알림을 성공적으로 전송했습니다. targetUserId="+requestDTO.getTargetUserId();
                } catch (FirebaseMessagingException e){
                    e.printStackTrace();
                    return "알림 보내기를 실패하였습니다. targetUserId="+requestDTO.getTargetUserId();
                }
            } else{
                return "서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId="+requestDTO.getTargetUserId();
            }
        } else{
            return "해당 유저가 존재하지 않습니다. targetUserId="+requestDTO.getTargetUserId();
        }
    }
}
