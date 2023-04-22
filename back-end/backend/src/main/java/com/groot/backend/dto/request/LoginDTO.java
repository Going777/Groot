package com.groot.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginDTO {
    private String userId;
    private String password;
}
