package com.groot.backend.service;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.entity.CommentEntity;

public interface CommentService {
    CommentEntity insertComment(CommentDTO commentDTO, Long userPK);
    CommentEntity updateComment(CommentDTO commentDTO);
    void deleteComment(Long commentId);
}
