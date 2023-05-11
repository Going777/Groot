package com.groot.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.groot.backend.dto.request.DiaryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "diaries")
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
    private Long userPK;

    @Column(name = "diary_check_id", insertable = false, updatable = false)
    private Long diaryId;

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

    @Column
    private Boolean isPotLast;

    @Column
    private Boolean isUserLast;

    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity userEntity;

    @ManyToOne(targetEntity = PotEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pot_id")
    @JsonBackReference
    private PotEntity potEntity;

    @ManyToOne(targetEntity = DiaryCheckEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_check_id")
    @JsonBackReference
    private DiaryCheckEntity diaryCheckEntity;

    @OneToMany(mappedBy = "diaryEntity", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<PlanEntity> planEntities;

    public DiaryEntity addCheckId (DiaryCheckEntity diaryEntity){
        DiaryEntity result = DiaryEntity.builder()
                .bug(diaryEntity.getBug())
                .sun(diaryEntity.getSun())
                .userPK(diaryEntity.getUserPK())
                .water(diaryEntity.getWater())
                .content(diaryEntity.getContent())
                .nutrients(diaryEntity.getNutrients())
                .pruning(diaryEntity.getPruning())
                .potId(diaryEntity.getPotId())
                .diaryId(diaryEntity.getId())
                .build();
        return result;
    }
}
