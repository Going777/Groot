package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PlantDetailDTO {

    private Long plantId;

    private String krName;

    private String sciName;

    private String grwType;

    private String waterCycle;

    private int minHumidity;

    private int maxHumidity;

    private int minGrwTemp;

    private int maxGrwTemp;

    private String description;

    private String place;

    private String mgmtLevel;

    private String mgmtDemand;

    private String mgmtTip;

    private String lightDemand;

    private String insectInfo;

    private String smellDegree;

    private String img;
}
