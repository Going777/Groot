package com.groot.backend.service;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.dto.response.CommentResponseDTO;
import com.groot.backend.entity.ArticleEntity;
import com.groot.backend.entity.CommentEntity;
import com.groot.backend.repository.ArticleRepository;
import com.groot.backend.repository.CommentRepository;
import com.groot.backend.repository.UserRepository;
import com.groot.backend.controller.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Override
    public CommentEntity insertComment(CommentDTO commentDTO, Long userPK) {
        CommentEntity comment = CommentEntity.builder()
                .userEntity(userRepository.findById(userPK).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")))
                .articleEntity(articleRepository.findById(commentDTO.getArticleId()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.")))
                .content(commentDTO.getContent())
                .build();
        CommentEntity result = commentRepository.save(comment);
        return result;
    }

    @Override
    public CommentEntity updateComment(CommentDTO commentDTO) {
        CommentEntity comment = CommentEntity.builder()
                .userEntity(userRepository.findById(commentDTO.getUserPK()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")))
                .articleEntity(articleRepository.findById(commentDTO.getArticleId()).orElseThrow(()->new CustomException(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다.")))
                .content(commentDTO.getContent())
                .build();
        CommentEntity result = commentRepository.save(comment);
        return result;
    }

    @Override
    public boolean deleteComment(Long commentId) {
        if(commentRepository.existsById(commentId)){
            return false;
        }
        commentRepository.deleteById(commentId);
        return true;
    }

    @Override
    public Page<CommentResponseDTO> readComment(Long articleId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<CommentEntity> commentEntities = commentRepository.findAllByArticleId(articleId, pageRequest);
        Page<CommentResponseDTO> result = new CommentResponseDTO().toDtoList(commentEntities);
        return result;
    }
}
