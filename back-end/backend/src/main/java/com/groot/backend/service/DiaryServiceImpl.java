package com.groot.backend.service;

import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.repository.DiaryRepository;
//import com.groot.backend.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiaryServiceImpl implements DiaryService{
    private final DiaryRepository diaryRepository;

//    @Autowired
//    private S3Uploader s3Uploader;


    @Override
    public DiaryEntity saveDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
        String storedFileName = null;
        if(!image.isEmpty()){
//            storedFileName = s3Uploader.upload(image, "images");
        }
        DiaryEntity diary = DiaryEntity.builder()
                .bug(diaryDTO.getBug()?true:false)
                .potId(diaryDTO.getPotId())
                .userPK(diaryDTO.getUserPK())
                .sun(diaryDTO.getSun()?true:false)
                .content(diaryDTO.getContent())
                .imgPath(storedFileName)
                .nutrients(diaryDTO.getNutrients()?true:false)
                .pruning(diaryDTO.getPruning()?true:false)
                .water(diaryDTO.getWater()?true:false)
                .build();
        return diaryRepository.save(diary);
    }

    @Override
    public DiaryEntity updateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
//        DiaryEntity diaryEntity = diaryRepository.findById(diaryDTO.getId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
        DiaryEntity diaryEntity = null;
        String storedFileName = null;
        if(!image.isEmpty()){
//            storedFileName = s3Uploader.upload(image, "images");
        }
        DiaryEntity newDiary = DiaryEntity.builder()
                .id(diaryEntity.getId())
                .userPK(diaryEntity.getUserPK())
                .potId(diaryEntity.getPotId())
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():diaryEntity.getBug())
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():diaryEntity.getSun())
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():diaryEntity.getPruning())
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():diaryEntity.getContent())
                .imgPath(storedFileName!=null?storedFileName:diaryEntity.getImgPath())
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():diaryEntity.getWater())
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():diaryEntity.getNutrients())
                .build();
        return diaryRepository.save(newDiary);
    }

    @Override
    public Boolean deleteDiary(Long userId, Long diaryId) {
        if(diaryRepository.existsById(diaryId)){
            diaryRepository.deleteById(diaryId);
            return true;
        }
        return false;
    }

    @Override
    public DiaryEntity detailDiary(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
    }
}
