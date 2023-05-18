package com.groot.backend.service;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.dto.response.CommentResponseDTO;
import com.groot.backend.entity.CommentEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    Boolean checkDelete(Long commentId, Long userPK);
    CommentEntity insertComment(CommentDTO commentDTO, Long userPK);
    CommentEntity updateComment(CommentDTO commentDTO);
    boolean deleteComment(Long commentId);
    List<CommentResponseDTO> readComment(Long articleId);
}
