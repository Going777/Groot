package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordDTO {
    @NotNull
    private Long userPK;
    @NotNull
    private String password;
    @NotNull
    private String newPassword;
}
