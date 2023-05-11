package com.groot.backend.repository;

import java.time.LocalDateTime;

public interface DiaryRepositoryCustom {
    Long updateIsLastByPotId(Long potId, LocalDateTime now);

    Long updateIsLastByUserId(Long userId, LocalDateTime now);
}
