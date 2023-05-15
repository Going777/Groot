package com.groot.backend.dto.request;

import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareStatusDTO {
    @NotNull
    private Long articleId;
    @NotNull
    private Long userPK;
}
