package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleBookmarkEntityPK implements Serializable {
    private Long userEntity;

    private Long articleEntity;


}
