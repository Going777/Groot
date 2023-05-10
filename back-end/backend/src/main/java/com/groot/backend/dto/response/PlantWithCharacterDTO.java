package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlantWithCharacterDTO {

    private PlantIdentificationDTO plantIdentificationDTO;

    private CharacterAssetDTO characterAssetDTO;
}
