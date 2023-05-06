package com.groot.backend.dto.response;

import com.groot.backend.entity.CharacterEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PotDetailDTO {

    private PotListDTO pot;

    private PlantDetailDTO plant;

    private CharacterDTO character;
}
