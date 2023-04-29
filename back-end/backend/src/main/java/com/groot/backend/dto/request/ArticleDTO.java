package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    private Long articleId;
    @NotNull
    private Long userPK;
    @NotNull
    private String category;
    @NotNull
    private String title;
    @NotNull
    private String content;

    private Long views;
    private Boolean shareStatus;

    private String shareRegion;
    private String[] tags;
}
