package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CharacterDTO {
    private Long characterId;
    private Integer type;
    private Integer level;
    private String glbPath;
    private String pngPath;
}