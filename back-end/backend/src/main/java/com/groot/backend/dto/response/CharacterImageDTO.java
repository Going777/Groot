package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CharacterImageDTO {

    private String grwType;

    private Integer level;

    private String pngPath;
}
