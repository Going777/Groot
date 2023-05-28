package com.groot.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class PotTransferDTO {

    @NotNull
    private Long userPK;

    @NotNull
    private Long potId;

    @NotNull
    private Long articleId;
}
