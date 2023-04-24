package com.groot.backend.service;

import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.entity.DiaryEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface DiaryService {
    DiaryEntity saveDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException;

    DiaryEntity updateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException;

    Boolean deleteDiary(Long userId, Long diaryId);

    DiaryEntity detailDiary(Long diaryId);
}
