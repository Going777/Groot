package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="article_bookmark")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ArticleBookmarkEntityPK.class)
public class ArticleBookmarkEntity {
    @Id
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;


    @Column(name = "article_id", insertable = false, updatable = false)
    private long articleId;
    @Id
    @ManyToOne(targetEntity = ArticleEntity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity articleEntity;
}
