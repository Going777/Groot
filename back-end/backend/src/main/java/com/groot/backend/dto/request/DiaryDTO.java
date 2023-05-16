package com.groot.backend.dto.request;

import com.groot.backend.entity.DiaryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDTO {

    private Long id;

    private Long potId;

    private Long userPK;

    private String content;

    private Boolean water;

    private Boolean pruning;

    private Boolean nutrients;

    private Boolean bug;

    private Boolean sun;

    private String imgPath;
}
