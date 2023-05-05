package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PotModifyDTO {

    private String potName;

    private double temperature;

    private int illuminance;

    private double humidity;

}
