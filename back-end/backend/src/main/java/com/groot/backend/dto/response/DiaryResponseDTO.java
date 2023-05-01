package com.groot.backend.dto.response;

import com.groot.backend.entity.DiaryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponseDTO {

    private Long id;

    private Long potId;

    private String potName;

    private Long userPK;

    private String nickName;

    private String profile;

    private String content;

    private String imgPath;

    private Boolean water;

    private Boolean pruning;

    private Boolean nutrients;

    private Boolean bug;

    private Boolean sun;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Page<DiaryResponseDTO> toDtoList(Page<DiaryEntity> diaryEntities) {
        Page<DiaryResponseDTO> dtoList = diaryEntities.map(a ->
                DiaryResponseDTO.builder()
                        .id(a.getId())
                        .potId(a.getPotId())
                        .potName(a.getPotEntity().getName())
                        .userPK(a.getUserEntity().getId())
                        .nickName(a.getUserEntity().getNickName())
                        .profile(a.getUserEntity().getProfile())
                        .imgPath(a.getImgPath())
                        .content(a.getContent())
                        .nutrients(a.getNutrients())
                        .water(a.getWater())
                        .bug(a.getBug())
                        .sun(a.getSun())
                        .createTime(a.getCreatedDate())
                        .updateTime(a.getLastModifiedDate())
                        .build());
        return dtoList;
    }

    public DiaryResponseDTO toDtoDiary(DiaryEntity diaryEntity){
        DiaryResponseDTO dto = DiaryResponseDTO.builder()
                .id(diaryEntity.getId())
                .potId(diaryEntity.getPotId())
                .potName(diaryEntity.getPotEntity().getName())
                .userPK(diaryEntity.getUserEntity().getId())
                .nickName(diaryEntity.getUserEntity().getNickName())
                .profile(diaryEntity.getUserEntity().getProfile())
                .imgPath(diaryEntity.getImgPath())
                .content(diaryEntity.getContent())
                .nutrients(diaryEntity.getNutrients())
                .water(diaryEntity.getWater())
                .bug(diaryEntity.getBug())
                .sun(diaryEntity.getSun())
                .createTime(diaryEntity.getCreatedDate())
                .updateTime(diaryEntity.getLastModifiedDate())
                .build();
        return dto;
    }
}

