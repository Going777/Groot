package com.groot.backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
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
import com.groot.backend.repository.NotificationRepository;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ChattingServiceImpl implements ChattingService{

    private final ChattingRepository chattingRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;
    public static final String COLLECTION_NAME = "chats";
    private final NotificationRepository notificationRepository;

    @Override
    public boolean insertChatting(ChatRequestDTO chatRequestDTO, Long userId) throws FirebaseAuthException {
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
                .chattingRoomId(chatRequestDTO.getRoomId())
                .page("chatting")
                .isRead(false)
                .content(body)
                .title(title)
                .receiver(user.get())
                .build();
        notificationRepository.save(noti);

        if(user.isPresent()) {
            if (user.get().getFirebaseToken() != null && user.get().getUserAlarmEntity().getChattingAlarm()) {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(user.get().getFirebaseToken());
                String uid = decodedToken.getUid();
                Notification notification = Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

                com.google.firebase.messaging.Message message = Message.builder()
                        .setToken(uid)
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
    public ChatDetailDTO getDetail(String roomId, Long userId) {
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
    public List<ChatResponseDTO> getList(Long userId) throws ExecutionException, InterruptedException {
        List<ChattingEntity> entityList = chattingRepository.findBySenderIdOrderByCreatedDateDesc(userId);
        List<ChatResponseDTO> dtoList = new ArrayList<>();
        for(ChattingEntity chattingEntity: entityList){
            UserEntity receiver = userRepository.findById(chattingEntity.getReceiverId()).orElseThrow();
//            Map<String, String> last = findLastMessage(chattingEntity.getRoomId());
            ChatResponseDTO dto = ChatResponseDTO.builder()
                    .userPK(receiver.getId())
                    .nickName(receiver.getNickName())
                    .profile(receiver.getProfile())
                    .roomId(chattingEntity.getRoomId())
//                    .LastMessage(last.get("lastMessage"))
//                    .LastTime(last.get("lastTime"))
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

    private Map<String, String> findLastMessage(String roomId) throws ExecutionException, InterruptedException {
        Map<String, String> result = new HashMap();
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<DocumentSnapshot> apiFuture = db.collection(COLLECTION_NAME).document(roomId).get();
        DocumentSnapshot documentSnapshot = apiFuture.get();
        result.put("LastMessage", documentSnapshot.get("lastMessage").toString());
        result.put("lastTime", documentSnapshot.get("lastTime").toString());
        return result;
    }

}
