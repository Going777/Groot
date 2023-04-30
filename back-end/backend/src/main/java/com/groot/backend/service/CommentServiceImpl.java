package com.groot.backend.service;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.entity.ArticleEntity;
import com.groot.backend.entity.CommentEntity;
import com.groot.backend.repository.CommentRepository;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleR
    @Override
    public CommentEntity insertComment(CommentDTO commentDTO, Long userPK) {
        CommentEntity comment = CommentEntity.builder()
                .userEntity(userRepository.findById(userPK).orElseThrow())
                .articleEntity(artic)
        return null;
    }

    @Override
    public CommentEntity updateComment(CommentDTO commentDTO) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {

    }
}
