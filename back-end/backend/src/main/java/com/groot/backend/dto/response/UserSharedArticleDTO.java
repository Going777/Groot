package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSharedArticleDTO {
    private Long userPK;
    private String nickName;
    private Long articleId;
    private String title;
    private String img;
}
