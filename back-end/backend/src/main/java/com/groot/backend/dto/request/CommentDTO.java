package com.groot.backend.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long articleId;
    private Long userPK;
    private String nickName;
    private String content;
}
