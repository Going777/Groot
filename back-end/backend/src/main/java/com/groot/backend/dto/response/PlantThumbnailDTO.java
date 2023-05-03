package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlantThumbnailDTO {

    private Long plantId;

    private String krName;

    private String img;
}
