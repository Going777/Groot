package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserDTO {
    @NotNull
    private String oauthProvider;
    @NotNull
    private String accessToken;
    private String nickName;
    private String firebaseToken;
}
