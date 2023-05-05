package com.groot.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PotRegisterDTO {

    @Schema(required = true)
    private Long plantId;

//    @Schema(required = true)
//    private Long characterId;

    @Schema(required = true)
    private String potName;

    @Schema(required = false)
    private double temperature;

    @Schema(required = false)
    private int illuminance;

    @Schema(required = false)
    private double humidity;
}
