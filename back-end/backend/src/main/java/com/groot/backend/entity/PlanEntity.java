package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.groot.backend.dto.response.PlanDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="plans")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pot_id", updatable = false, insertable = false)
    private Long potId;

    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userPK;

    @Column(name = "code")
    private Integer code;

    @Column(name = "dateTime")
    private LocalDateTime dateTime;

    @Column(name = "done")
    private boolean done;

    @ManyToOne(targetEntity = PotEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pot_id")
    @JsonManagedReference
    private PotEntity potEntity;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private UserEntity userEntity;

    public PlanDTO toPlanDTO(){
        PlanDTO result = PlanDTO.builder()
                .code(this.code)
                .imgPath(this.potEntity.getImgPath())
                .potName(this.potEntity.getName())
                .dateTime(this.dateTime)
                .potId(this.potId)
                .userPK(this.userPK)
                .build();
        return result;
    }
}
