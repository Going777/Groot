package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponseDTO {
    private String category;
    private List<String> imgs;
    private Long userPK;
    private String nickName;
    private String profile;
    private String title;
    private List<String> tags;
    private Long views;
    private int commentCnt;
    private boolean bookmark;
    private String shareRegion;
    private String content;
    private Boolean shareStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CommentResponseDTO> comments;
}
