package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="articles_bookmarks")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ArticleBookmarkEntityPK.class)
public class ArticleBookmarkEntity {
    @Id
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",insertable = false, updatable = false)
    private UserEntity userEntity;

    @Column(name = "user_id", insertable = false, updatable = false)
    private long userPK;

    @Id
    @ManyToOne(targetEntity = ArticleEntity.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",insertable = false, updatable = false)
    private ArticleEntity articleEntity;

    @Column(name = "article_id", insertable = false, updatable = false)
    private long articleId;
}
