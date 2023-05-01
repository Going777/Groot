package com.groot.backend.service;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.dto.response.CommentResponseDTO;
import com.groot.backend.entity.CommentEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    CommentEntity insertComment(CommentDTO commentDTO, Long userPK);
    CommentEntity updateComment(CommentDTO commentDTO);
    boolean deleteComment(Long commentId);
    Page<CommentResponseDTO> readComment(Long articleId, Integer page, Integer size);
}
