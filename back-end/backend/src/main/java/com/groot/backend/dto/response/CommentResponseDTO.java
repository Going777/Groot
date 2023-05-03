package com.groot.backend.dto.response;

import com.groot.backend.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

    private Long id;
    private Long userPK;
    private String nickName;
    private String profile;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Page<CommentResponseDTO> toDtoList(Page<CommentEntity> commentEntities) {
        Page<CommentResponseDTO> dtoList = commentEntities.map(a ->
                CommentResponseDTO.builder()
                        .id(a.getId())
                        .userPK(a.getUserEntity().getId())
                        .nickName(a.getUserEntity().getNickName())
                        .profile(a.getUserEntity().getProfile())
                        .content(a.getContent())
                        .createTime(a.getCreatedDate())
                        .updateTime(a.getLastModifiedDate())
                        .build());
        return dtoList;
    }
}
