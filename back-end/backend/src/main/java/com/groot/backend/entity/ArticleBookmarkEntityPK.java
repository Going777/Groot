package com.groot.backend.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ArticleBookmarkEntityPK implements Serializable {
    private Long userEntity;

    private Long articleEntity;
}
