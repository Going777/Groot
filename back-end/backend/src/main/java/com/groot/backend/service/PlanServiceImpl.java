package com.groot.backend.service;

import com.groot.backend.entity.DiaryCheckEntity;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.entity.PlanEntity;
import com.groot.backend.entity.PotEntity;
import com.groot.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanServiceImpl implements PlanService{
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final PotRepository potRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryCheckRepository diaryCheckRepository;


    @Override
    public void deletePlan(Long planId) {

        // plan 삭제
        PlanEntity plan = planRepository.findById(planId).orElseThrow();
        planRepository.deleteById(planId);

        // 관련 diary 수정
        DiaryEntity diary = diaryRepository.findById(plan.getDiaryId()).orElseThrow();
        DiaryEntity newDiary = DiaryEntity.builder()
                .id(plan.getDiaryId())
                .bug(diary.getBug())
                .sun(diary.getSun())
                .imgPath(diary.getImgPath())
                .water(plan.getCode()==0?false:diary.getWater())
                .potEntity(diary.getPotEntity())
                .pruning(diary.getPruning())
                .nutrients(plan.getCode()==1?false:diary.getNutrients())
                .diaryId(diary.getDiaryId())
                .isUserLast(diary.getIsUserLast())
                .diaryCheckEntity(diary.getDiaryCheckEntity())
                .isPotLast(diary.getIsPotLast())
                .isUserLast(diary.getIsUserLast())
                .build();

        // 유효한 정보가 없을 경우 (모두 false인 경우) diary 삭제
        if(!newDiary.getNutrients() && !newDiary.getBug() && !newDiary.getWater() && !newDiary.getPruning() && newDiary.getSun() && newDiary.getContent()==null && newDiary.getImgPath()==null){
            diaryRepository.deleteById(plan.getDiaryId());
            // 삭제한 diary가 isUserLast 였던 경우
            if(diary.getIsUserLast()){
                LocalDateTime startDateTime = LocalDateTime.of(LocalDate.from(diary.getCreatedDate()), LocalTime.of(0, 0, 0));
                LocalDateTime endDateTime = LocalDateTime.of(LocalDate.from(diary.getCreatedDate()), LocalTime.of(23, 59, 59));
                DiaryEntity find = diaryRepository.findTop1ByUserPKAndCreatedDateBetweenOrderByCreatedDateDesc(diary.getUserPK(), startDateTime, endDateTime);
                diaryRepository.updateIsUserLastById(find.getId(), !diary.getIsUserLast());
            }
            // 삭제한 diary가 isPotLast 였던 경우
            if(diary.getIsPotLast()){
                LocalDateTime startDateTime = LocalDateTime.of(LocalDate.from(diary.getCreatedDate()), LocalTime.of(0, 0, 0));
                LocalDateTime endDateTime = LocalDateTime.of(LocalDate.from(diary.getCreatedDate()), LocalTime.of(23, 59, 59));
                DiaryEntity find = diaryRepository.findTop1ByPotIdAndCreatedDateBetweenOrderByCreatedDateDesc(diary.getUserPK(), startDateTime, endDateTime);
                diaryRepository.updateIsPotLastById(find.getId(), !diary.getIsPotLast());
            }
        }


        // diaryCheck 수정
        DiaryCheckEntity checkEntity = diaryCheckRepository.findById(diary.getDiaryId()).orElseThrow();
        DiaryCheckEntity newDiaryCheck = DiaryCheckEntity.builder()
                .id(checkEntity.getId())
                .bug(checkEntity.getBug())
                .nutrients(plan.getCode()==0?!checkEntity.getNutrients():checkEntity.getNutrients())
                .water(plan.getCode()==1?!checkEntity.getWater():checkEntity.getWater())
                .imgPath(checkEntity.getImgPath())
                .pruning(checkEntity.getPruning())
                .content(checkEntity.getContent())
                .sun(checkEntity.getSun())
                .userEntity(checkEntity.getUserEntity())
                .potEntity(checkEntity.getPotEntity())
                .build();
        diaryCheckRepository.save(newDiaryCheck);

        // pot exp, level 수정
        PotEntity pot = potRepository.findById(plan.getPotId()).orElseThrow();
        int tempExp = pot.getExperience()-10;
        int tempLevel = pot.getLevel();
        if(tempExp < 0){
            tempLevel -= 1;
            tempExp += tempLevel*10;
        }
        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
    }


}
