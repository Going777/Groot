package com.groot.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void deletePlan(Long planId) {

        // plan 삭제
        PlanEntity plan = planRepository.findById(planId).orElseThrow();
//        planRepository.deleteById(planId);
        planRepository.updateDoneById(planId);

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

    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Seoul") // 오전 11시
    @Override
    public void alarmPlan(){
        LocalDateTime now = LocalDateTime.now().plusHours(9);
        LocalDateTime start = LocalDateTime.of(LocalDate.from(now), LocalTime.of(0, 0, 0));
        LocalDateTime end = LocalDateTime.of(LocalDate.from(now), LocalTime.of(23, 59, 59));

        // 지났지만 done이 0인 친구들 하루씩 미뤄주기\
        planRepository.updateDateTimeByDoneAndDateTimeBetween(false, start.minusDays(1), end.minusDays(1));


        List<PlanEntity> planEntities = planRepository.findAllByDoneAndDateTimeBetween(false, start, end);

        for(PlanEntity plan : planEntities){
            PotEntity pot = potRepository.findById(plan.getPotId()).orElseThrow();
            String title = "";
            String body = "";
            if(plan.getCode()==0) {
                title = "물주기 알림";
                body = pot.getName() + "에게 물을 줄 시간입니다!";
            } else {
                title = "영양제 알림";
                body = pot.getName() + "에게 영양제를 줄 시간입니다!";
            }
            Optional<UserEntity> user = userRepository.findById(plan.getUserPK());
            if(user.isPresent()) {
                if (user.get().getFirebaseToken() != null) {
                    Notification notification = Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build();

                    com.google.firebase.messaging.Message message = Message.builder()
                            .setToken(user.get().getFirebaseToken())
                            .setNotification(notification)
                            .build();

                    try {
                        firebaseMessaging.send(message);
//                    return "알림을 성공적으로 전송했습니다. targetUserId="+recieiver.getId();
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

}
