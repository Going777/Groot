package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CommentDTO {
    private Long articleId;
    private Long userPK;
    private String nickName;
    private String content;
}
