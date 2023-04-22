package com.groot.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="article")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleEntity extends BaseEntity{
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userPK;

    @Column(name = "category", nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long views;

    @Column(name = "share_status")
    private Boolean shareStatus;

    @Column(name = "share_region")
    private String shareRegion;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "articleEntity", cascade = CascadeType.REMOVE)
    private List<ArticleImageEntity> articleImageEntityList;
}
