package com.groot.backend.dto.response;

import com.groot.backend.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<CommentResponseDTO> toDtoList(List<CommentEntity> commentEntities) {
        List dtoList = new ArrayList();
        for(CommentEntity comment: commentEntities){
            dtoList.add(CommentResponseDTO.builder()
                    .id(comment.getId())
                    .userPK(comment.getUserEntity().getId())
                    .nickName(comment.getUserEntity().getNickName())
                    .profile(comment.getUserEntity().getProfile())
                    .content(comment.getContent())
                    .createTime(comment.getCreatedDate())
                    .updateTime(comment.getLastModifiedDate())
                    .build());
        }

        return dtoList;
    }
}
