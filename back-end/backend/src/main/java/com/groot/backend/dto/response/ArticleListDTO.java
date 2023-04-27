package com.groot.backend.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListDTO {
    private Long articleId;
    private String category;
    private String[] imgs;
    private Long userPK;
    private String nickName;
    private String profile;
    private String title;
    private List<String> tags;
    private Long views;
    private int commentCnt;
    private boolean bookmark;
    private String shareRegion;
    private Boolean shareStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
