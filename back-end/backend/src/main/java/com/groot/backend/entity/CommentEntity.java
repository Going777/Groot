package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="comments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userPK;

    @Column(name = "article_id", insertable = false, updatable = false)
    private Long articleId;

    @Column(nullable = false)
    private String content;

    @ManyToOne(targetEntity = ArticleEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    @JsonBackReference
    private ArticleEntity articleEntity;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity userEntity;
}
