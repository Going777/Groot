package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PlanDTO {
    private Long potId;
    private Long userPK;
    private Integer code;  // 0이면 물, 1이면 영양제
//    private LocalDateTime dateTime;
    private String potName;
    private String imgPath;
    private boolean done;
}
