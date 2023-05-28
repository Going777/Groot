package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PotTransferInfoDTO {

    private Long articleId;

    private String articleTitle;

    private String articleImage;

    private Long userPK;

    private String userNickname;

    private String userImage;

    private String potName;

    private String plantName;

    private LocalDateTime createdTime;

}
