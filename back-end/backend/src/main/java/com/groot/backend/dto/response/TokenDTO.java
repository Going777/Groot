package com.groot.backend.dto.response;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    private String grantType;
    @NotNull
    private String accessToken;
    @NotNull
    private String refreshToken;
    private Long userPK;
}
