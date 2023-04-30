package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PotDTO {
    private Long id;

    private Long userId;

    private Long plantId;

    private Long characterId;

    private String potName;

    private double temperature;

    private int illuminance;

    private double humidity;
}
