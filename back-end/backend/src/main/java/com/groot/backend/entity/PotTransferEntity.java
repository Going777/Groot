package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "pots_transfers")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PotTransferEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    @JsonBackReference
    private UserEntity fronUserEntity;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    @JsonBackReference
    private UserEntity toUserEntity;

    @OneToOne(targetEntity = PotEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pot_id")
    private PotEntity potEntity;

    @OneToOne(targetEntity = ArticleEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity articleEntity;

}
