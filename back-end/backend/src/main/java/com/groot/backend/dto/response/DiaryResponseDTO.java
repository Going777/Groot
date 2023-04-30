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
}

