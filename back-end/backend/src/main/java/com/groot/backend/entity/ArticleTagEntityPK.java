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
public class ArticleTagEntityPK implements Serializable {

    private Long articleEntity;
    private Long tagEntity;
}
