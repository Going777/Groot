package com.groot.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="article_tag")
@IdClass(ArticleTagEntityPK.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTagEntity implements Serializable {
    @Id
    @ManyToOne(targetEntity = ArticleEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id",insertable = false, updatable = false)
    private ArticleEntity articleEntity;

    @Column(name = "article_id", insertable = false, updatable = false)
    private long articleId;


    @Id
    @ManyToOne(targetEntity = TagEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id",insertable = false, updatable = false)
    private TagEntity tagEntity;

    @Column(name = "tag_id", insertable = false, updatable = false)
    private long tagId;
}
