package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMRequestDTO {
    private Long targetUserId;
    private String title;
    private String body;

    @Builder
    public FCMRequestDTO(Long targetUserId, String title, String body){
        this.targetUserId = targetUserId;
        this.title = title;
        this.body = body;
    }
}
