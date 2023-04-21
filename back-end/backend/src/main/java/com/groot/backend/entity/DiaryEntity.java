package com.groot.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "diary")
public class DiaryEntity extends BaseEntity{
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(name = "pot_id", insertable = false, updatable = false)
    private Long potId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userPK;

    @Column(name = "img_path")
    private String imgPath;

    @Column
    private String content;

    @Column
    private Boolean water;

    @Column
    private Boolean pruning;

    @Column
    private Boolean nutrients;

    @Column
    private Boolean bug;

    @Column
    private Boolean sun;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(targetEntity = PotEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pot_id")
    private PotEntity potEntity;
}
