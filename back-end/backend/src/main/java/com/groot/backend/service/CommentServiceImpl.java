package com.groot.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.dto.response.CommentResponseDTO;
import com.groot.backend.dto.response.NotificationResponseDTO;
import com.groot.backend.entity.ArticleEntity;
import com.groot.backend.entity.CommentEntity;
import com.groot.backend.entity.NotificationEntity;
import com.groot.backend.entity.UserEntity;
import com.groot.backend.repository.ArticleRepository;
import com.groot.backend.repository.CommentRepository;
import com.groot.backend.repository.NotificationRepository;
import com.groot.backend.repository.UserRepository;
import com.groot.backend.controller.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public Boolean checkDelete(Long commentId, Long userPK) {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."));
        if(userPK == comment.getUserPK()){
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public CommentEntity insertComment(CommentDTO commentDTO, Long userPK) {
        UserEntity writer = userRepository.findById(userPK).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        ArticleEntity article = articleRepository.findById(commentDTO.getArticleId()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."));
        CommentEntity comment = CommentEntity.builder()
                .userEntity(writer)
                .articleEntity(article)
                .content(commentDTO.getContent())
                .build();
        CommentEntity result = commentRepository.save(comment);

        UserEntity recieiver = articleRepository.findById(commentDTO.getArticleId()).orElseThrow().getUserEntity();
//        notificationService.send(recieiver, writer.getNickName()+"님이 '"+article.getTitle()+"'글에 댓글을 작성하였습니다.", "", "article", commentDTO.getArticleId());

        String title = "댓글 알림";
        String body = writer.getNickName()+"님이 '"+article.getTitle()+"'글에 댓글을 작성하였습니다.";

        Optional<UserEntity> user = userRepository.findById(recieiver.getId());

        NotificationEntity noti = NotificationEntity.builder()
                .contentId(article.getId())
                .page("article")
                .isRead(false)
                .content(body)
                .title(title)
                .receiver(user.get())
                .build();

        if(user.isPresent() && user.get().getUserAlarmEntity().getCommentAlarm()) {
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
                    notificationRepository.save(noti);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public CommentEntity updateComment(CommentDTO commentDTO) {
        CommentEntity comment = CommentEntity.builder()
                .id(commentDTO.getId())
                .userEntity(userRepository.findById(commentDTO.getUserPK()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")))
                .articleEntity(articleRepository.findById(commentDTO.getArticleId()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.")))
                .content(commentDTO.getContent())
                .build();
        CommentEntity result = commentRepository.save(comment);
        return result;
    }


    @Override
    public boolean deleteComment(Long commentId) {
        if(!commentRepository.existsById(commentId)){
            return false;
        }
        commentRepository.deleteById(commentId);
        return true;
    }

    @Override
    public List<CommentResponseDTO> readComment(Long articleId) {
//        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdDate"));
        List<CommentEntity> commentEntities = commentRepository.findAllByArticleIdOrderByCreatedDateAsc(articleId);
        List<CommentResponseDTO> result = new CommentResponseDTO().toDtoList(commentEntities);
        return result;
    }
}
