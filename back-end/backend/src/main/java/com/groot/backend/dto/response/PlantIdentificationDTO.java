package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class PlantIdentificationDTO {
    private Long plantId;

    private String krName;

    private String sciName;

    private String grwType;

    private String mgmtLevel;

    private int score;
}
