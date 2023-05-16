package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmDTO {
    private Boolean waterAlarm;
    private Boolean commentAlarm;
    private Boolean chattingAlarm;
}
