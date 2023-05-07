package com.groot.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagRankDTO {
    private String tag;
    private Double count;

    public static TagRankDTO convertToTagRankDTO(ZSetOperations.TypedTuple<String> stringTypedTuple) {
        TagRankDTO result = TagRankDTO.builder()
                .tag(stringTypedTuple.getValue())
                .count(stringTypedTuple.getScore())
                .build();
        return result;
    }
}
