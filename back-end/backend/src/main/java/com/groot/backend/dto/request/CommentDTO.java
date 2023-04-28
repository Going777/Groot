package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CommentDTO {
    Long articleId;
    Long userPK;
    String nickName;
    String content;
}
