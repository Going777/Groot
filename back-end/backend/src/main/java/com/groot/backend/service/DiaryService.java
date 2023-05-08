package com.groot.backend.service;

import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.dto.response.DiaryResponseDTO;
import com.groot.backend.entity.DiaryCheckEntity;
import com.groot.backend.entity.DiaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


public interface DiaryService {

    DiaryDTO isExistByCreatedDate(Long potId);

    DiaryEntity saveDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException;

    DiaryEntity saveAndUpdateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO, DiaryDTO check) throws IOException;

    DiaryEntity updateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException;

    Boolean deleteDiary(Long diaryId);

    DiaryResponseDTO detailDiary(Long diaryId);

    DiaryCheckEntity checkDiary(Long diaryId);

    Page<DiaryResponseDTO> diaryList(Long userId, Integer page, Integer size);

    Page<DiaryResponseDTO> diaryListByPotId(Long potId, Integer page, Integer size);

    List<DiaryCheckEntity> weeklyDiaries(Long userId);
}
