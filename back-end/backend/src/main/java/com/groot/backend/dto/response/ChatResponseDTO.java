package com.groot.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponseDTO {
    private Long userPK;
    private String nickName;
    private String profile;
    private String roomId;
//    private String LastMessage;
//    private String LastTime;
}
