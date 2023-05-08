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
@Table(name = "diary_check")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCheckEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @JsonBackReference
    private UserEntity userEntity;

    @ManyToOne(targetEntity = PotEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pot_id")
    @JsonBackReference
    private PotEntity potEntity;

    @OneToMany(mappedBy = "diaryCheckEntity", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<DiaryEntity> diaryEntities;

    public DiaryDTO toDTO (DiaryCheckEntity diaryEntity){
        DiaryDTO result = DiaryDTO.builder()
                .bug(diaryEntity.getBug())
                .sun(diaryEntity.getSun())
                .userPK(diaryEntity.getUserPK())
                .water(diaryEntity.getWater())
                .nutrients(diaryEntity.getNutrients())
                .pruning(diaryEntity.getPruning())
                .potId(diaryEntity.getPotId())
                .build();
        return result;
    }
}
