package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long userPK;
    private String profile;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
