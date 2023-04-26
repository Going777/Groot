package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDTO {

    private Long id;

    private Long potId;

    private Long userId;

    private String content;

    private Boolean water;

    private Boolean pruning;

    private Boolean nutrients;

    private Boolean bug;

    private Boolean sun;
}
