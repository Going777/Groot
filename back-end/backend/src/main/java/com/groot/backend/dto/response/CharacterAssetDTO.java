package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CharacterAssetDTO {

    private String glbPath;

    private String pngPath;
}
