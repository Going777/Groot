package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "diary")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pot_id", insertable = false, updatable = false)
    private Long potId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

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
    @JsonBackReference
    private UserEntity userEntity;

    @ManyToOne(targetEntity = PotEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pot_id")
    @JsonBackReference
    private PotEntity potEntity;
}
