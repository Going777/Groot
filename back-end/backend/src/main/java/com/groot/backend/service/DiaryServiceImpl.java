package com.groot.backend.service;

import com.groot.backend.controller.NotificationController;
import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.dto.response.DiaryResponseDTO;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.entity.PotEntity;
import com.groot.backend.entity.UserEntity;
//import com.groot.backend.repository.DiaryListRepository;
import com.groot.backend.repository.DiaryRepository;
//import com.groot.backend.util.S3Uploader;
import com.groot.backend.repository.PotRepository;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiaryServiceImpl implements DiaryService{
    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final PotRepository potRepository;

//    private final DiaryListRepository diaryListRepository;

    private final S3Service s3Service;


    @Override
    public DiaryEntity isExistByCreatedDate(Long userPK) {
        return diaryRepository.existsByUserCreatedDate(userPK);
    }

    @Transactional
    @Override
    public DiaryEntity saveDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
        String storedFileName = null;
        int score = 0;
        if(image != null){
            storedFileName = s3Service.upload(image, "diary");
        }
        PotEntity pot = potRepository.findById(diaryDTO.getPotId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 화분을 찾을 수 없습니다."));
        DiaryEntity diary = DiaryEntity.builder()
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():false)
                .potEntity(pot)
                .userEntity(userRepository.findById(userId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.")))
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():false)
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():null)
                .imgPath(storedFileName)
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():false)
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():false)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():false)
                .build();
        int[] checkList = {diaryDTO.getWater()!=null?10:0, diaryDTO.getSun()!=null?10:0, diaryDTO.getPruning()!=null?30:0, diaryDTO.getNutrients()!=null?30:0, diaryDTO.getBug()!=null?10:0, diaryDTO.getContent()!=null?10:0};
        for(int i: checkList){
            score += i;
        }
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        if(tempExp>tempLevel*100){
            tempExp -= tempLevel*100;
            tempLevel+=1;
        }
        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
//        score += Collections.frequency(checkList, true);
//        List<Long> subsToList = subscribeRepository.findSubscribeTo(principalDetails.getUser().getId());
        Long id = userId;
        UserEntity user = userRepository.findById(id).orElseThrow();

            SseEmitter sseEmitter = NotificationController.sseEmitterMap.get(id);
            try {
                sseEmitter.send(SseEmitter.event().name("notification").data("새로운 글을 업로드했습니다!"));
            } catch (Exception e) {
                NotificationController.sseEmitterMap.remove(id);
            }
//        }

        return diaryRepository.save(diary);
    }

    @Override
    public DiaryEntity updateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
        DiaryEntity diaryEntity = diaryRepository.findById(diaryDTO.getId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
        String storedFileName = null;
        if(diaryEntity.getImgPath()!=null) {
            s3Service.delete(diaryEntity.getImgPath());
        }
        if(image != null){
            storedFileName = s3Service.upload(image, "diary");
        }
        PotEntity pot = diaryEntity.getPotEntity();
        DiaryEntity newDiary = DiaryEntity.builder()
                .id(diaryEntity.getId())
                .userEntity(diaryEntity.getUserEntity())
                .potEntity(pot)
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():diaryEntity.getBug())
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():diaryEntity.getSun())
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():diaryEntity.getPruning())
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():diaryEntity.getContent())
                .imgPath(storedFileName)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():diaryEntity.getWater())
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():diaryEntity.getNutrients())
                .build();
        Integer score = 0;
        int[] beforeScore = {!diaryEntity.getWater()?-10:0, diaryEntity.getSun()?-10:0, diaryEntity.getPruning()?-30:0, diaryEntity.getNutrients()?-30:0, diaryEntity.getBug()?-10:0, diaryEntity.getContent()!=null?-10:0};
        for(int i: beforeScore){
            score += i;
        }
        int[] checkList = {diaryDTO.getWater()!=null?10:0, diaryDTO.getSun()!=null?10:0, diaryDTO.getPruning()!=null?30:0, diaryDTO.getNutrients()!=null?30:0, diaryDTO.getBug()!=null?10:0, diaryDTO.getContent()!=null?10:0};
        for(int i: checkList){
            score += i;
        }
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        if(tempExp>pot.getLevel()*100){
            tempExp -= pot.getLevel()*100;
            tempLevel+=1;
        }else if(tempExp < 0){
            tempLevel -= 1;
            tempExp += tempLevel*100;
        }
        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
        return diaryRepository.save(newDiary);
    }

    @Override
    public Boolean deleteDiary(Long diaryId) {
        if(diaryRepository.existsById(diaryId)){
            DiaryEntity diaryEntity = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
            if(s3Service.delete(diaryEntity.getImgPath())<0) return false;
            diaryRepository.deleteById(diaryId);
            return true;
        }
        return false;
    }

    @Override
    public DiaryResponseDTO detailDiary(Long diaryId) {
        DiaryEntity diary = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
        DiaryResponseDTO result = new DiaryResponseDTO().toDtoDiary(diary);
        return result;
    }

    @Override
    public Page<DiaryResponseDTO> diaryList(Long userId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<DiaryEntity> diaryEntities = diaryRepository.findAllByUserId(userId, pageRequest);
        Page<DiaryResponseDTO> result = new DiaryResponseDTO().toDtoList(diaryEntities);
        return result;
    }

    @Override
    public Page<DiaryResponseDTO> diaryListByPotId(Long potId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<DiaryEntity> diaryEntityList = diaryRepository.findAllByPotId(potId, pageRequest);

        Page<DiaryResponseDTO> result = new DiaryResponseDTO().toDtoList(diaryEntityList);
        return result;
    }

    @Override
    public List<DiaryEntity> weeklyDiaries() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.DATE, -7);
        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String nowFormat = sDate.format(now);
        String pastFormat = sDate.format(cal.getTime());

        log.info(nowFormat);

        List<DiaryEntity> result = diaryRepository.findAllByDate(nowFormat);
        return result;
    }


}
