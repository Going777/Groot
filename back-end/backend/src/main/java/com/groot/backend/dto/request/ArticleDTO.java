package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDTO {
    private Long articleId;
    private Long userPK;

    private String category;

    private String title;

    private String content;

    private Long views;

    private Boolean shareStatus;

    private String shareRegion;
    private String[] tags;
}
