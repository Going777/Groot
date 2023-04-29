package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="article_image")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImageEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id", insertable = false, updatable = false)
    private Long articleId;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String img;

    @ManyToOne(targetEntity = ArticleEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity articleEntity;
}
