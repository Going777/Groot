package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userPK;
    private String userId;
    private String nickName;
    private String profile;
    private Long registerDate;
    private Boolean waterAlarm;
    private Boolean commentAlarm;
    private Boolean chattingAlarm;
}
