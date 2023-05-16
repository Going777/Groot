package com.groot.backend.repository;

import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

public interface DiaryRepositoryCustom {
    Long updateIsLastByPotId(Long potId, LocalDateTime now);

    Long updateIsLastByUserId(Long userId, LocalDateTime now);

    Long updateIsUserLastById(Long id, Boolean setUserLast);

    Long updateIsPotLastById(Long id, Boolean setPotLast);

    Long updateIsPotLastToTrueByPotIdAndDateTime(Long potId, LocalDateTime dateTime);

    Long updateIsPotLastToTrueByUserIdAndDateTime(Long userId, LocalDateTime dateTime);
}
