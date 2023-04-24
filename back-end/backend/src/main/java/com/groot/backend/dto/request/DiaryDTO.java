package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
}
