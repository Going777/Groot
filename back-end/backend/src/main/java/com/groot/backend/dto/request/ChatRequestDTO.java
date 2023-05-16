package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRequestDTO {
    private Long userPK;
    private String roomId;
}
