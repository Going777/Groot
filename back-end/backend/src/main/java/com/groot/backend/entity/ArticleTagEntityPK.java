package com.groot.backend.entity;

import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;
@Getter
public class ArticleTagEntityPK implements Serializable {

    private Long articleEntity;
    private Long tagEntity;
}
