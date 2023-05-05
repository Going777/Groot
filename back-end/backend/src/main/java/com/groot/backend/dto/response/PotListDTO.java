package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PotListDTO {

    private Long potId;

    private Long plantId;

    private String potName;

    private String imgPath;

    private String plantKrName;

    private Integer dates;

    private LocalDateTime createdTime;

    private LocalDateTime waterDate;

    private LocalDateTime nutrientsDate;

    private LocalDateTime pruningDate;

    private Boolean survival;

    private int level;

    private Long characterId;
}
