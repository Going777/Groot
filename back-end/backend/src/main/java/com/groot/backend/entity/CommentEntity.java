package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="comment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity extends BaseEntity{
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userPK;

    @Column(name = "article_id", insertable = false, updatable = false)
    private Long articleId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(targetEntity = ArticleImageEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity articleEntity;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
