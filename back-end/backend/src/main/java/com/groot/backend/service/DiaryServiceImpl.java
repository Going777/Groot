package com.groot.backend.service;

import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.dto.response.DiaryResponseDTO;
import com.groot.backend.dto.response.PlanDTO;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
//import com.groot.backend.util.S3Uploader;
import com.groot.backend.util.PlantCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DiaryServiceImpl implements DiaryService{
    private final PlanRepository planRepository;
    private final PlantRepository plantRepository;
    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    private final PotRepository potRepository;

    private final DiaryCheckRepository diaryCheckRepository;

    private final S3Service s3Service;

    private static int[] monthDate = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Override
    public DiaryCheckEntity isExistByCreatedDate(Long potId) {
        return diaryCheckRepository.existsByPotIdCreatedDate(potId);
    }

    @Transactional
    @Override
    public DiaryEntity saveDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
        String storedFileName = null;
        int score = 0;
        if(image != null){
            storedFileName = s3Service.upload(image, "diary");
        }
        log.info("potId: "+diaryDTO.getPotId());
        PotEntity pot = potRepository.findById(diaryDTO.getPotId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 화분을 찾을 수 없습니다."));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));
        // check 테이블에 저장
        DiaryCheckEntity checkdiary = DiaryCheckEntity.builder()
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():false)
                .potEntity(pot)
                .userEntity(user)
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():false)
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():null)
                .imgPath(storedFileName)
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():false)
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():false)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():false)
                .build();

        // check 테이블에 저장 후 해당 id를 가져와 diary 테이블에 함께 저장
        DiaryEntity diary = DiaryEntity.builder()
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():false)
                .potEntity(pot)
                .userEntity(user)
                .diaryCheckEntity(checkdiary)
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():false)
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():null)
                .imgPath(storedFileName)
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():false)
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():false)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():false)
                .isPotLast(true)
                .isUserLast(true)
                .build();

        // 이전 마지막값 false로 수정
        LocalDateTime startDateTime = LocalDateTime.of(LocalDate.from(LocalDateTime.now()), LocalTime.of(0, 0, 0));
        LocalDateTime endDateTime = LocalDateTime.of(LocalDate.from(LocalDateTime.now()), LocalTime.of(23, 59, 59));
        DiaryEntity find = diaryRepository.findTop1ByUserPKAndCreatedDateBetweenOrderByCreatedDateDesc(diary.getUserPK(), startDateTime, endDateTime);
        if(find!=null) {
            diaryRepository.updateIsUserLastById(find.getId(), false);
        }

//        diaryRepository.updateIsLastByUserId(user.getId(), LocalDateTime.now());
//        log.info("result: "+result.getId());

        // 물주기 일정 추가
        if(diary.getWater()) {
            addDonePlan(user, pot, 0, diary);
        }
        //영양제 일정 추가
        if(diary.getNutrients()) {
            addDonePlan(user, pot, 1, diary);
            // 해당 미션 완료 표시 및 실행 날짜 업데이트
//            planRepository.updateDoneAndDateTimeByCodeAndPotId(0, pot.getId());
//            log.info("plan에 미션 완료 표시");
//            addPlan(user, pot, 1, LocalDateTime.now());
        }


        // 화분 경험치 계산
        int[] checkList = {diaryDTO.getWater()!=null?10:0, diaryDTO.getSun()!=null?10:0, diaryDTO.getPruning()!=null?30:0, diaryDTO.getNutrients()!=null?30:0, diaryDTO.getBug()!=null?10:0, diaryDTO.getContent()!=null?10:0, image!=null?10:0};
        for(int i: checkList){
            score += i;
        }
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        while(tempExp>=tempLevel*10){  // 경험치는 해당 레벨의 x10을 얻어야 레벨업 가능
            tempExp -= tempLevel*10;
            tempLevel+=1;
        }

        // pot entity 수정본 만들기
        LocalDateTime now = LocalDateTime.now();
        PotEntity newPot = PotEntity.builder()
                        .id(pot.getId())
                .humidity(pot.getHumidity())
                .illuminance(pot.getIlluminance())
                .imgPath(pot.getImgPath())
                .name(pot.getName())
                .userEntity(user)
                .diaryEntities(pot.getDiaryEntities())
                .plantId(pot.getPlantId())
                .plantKrName(pot.getPlantKrName())
                .temperature(pot.getTemperature())
                .share(pot.getShare())
                .nutrientsDate(diary.getNutrients()?now:pot.getNutrientsDate())
                .pruningDate(diary.getPruning()?now:pot.getPruningDate())
                .saleDate(pot.getSaleDate())
                .level(tempLevel)
                .experience(tempExp)
                .waterDate(diary.getWater()?now:pot.getWaterDate())
                .survival(pot.getSurvival())
                .plantEntity(pot.getPlantEntity())
                .planEntities(pot.getPlanEntities())
                                .build();
//        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
        potRepository.save(newPot);
        diaryCheckRepository.save(checkdiary);
        return diaryRepository.save(diary);
    }

    public DiaryEntity saveAndUpdateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO, DiaryDTO diaryCheck) throws IOException {
//        DiaryCheckEntity diaryCheck = diaryCheckRepository.findById(diaryDTO.getDiaryId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "당일 다이어리 정보를 찾을 수 없습니다."));
        String storedFileName = null;

        if(image != null){
            storedFileName = s3Service.upload(image, "diary");
        }
        PotEntity pot = potRepository.findById(diaryCheck.getPotId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 화분의 정보를 찾을 수 없습니다."));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        // check 테이블 업데이트
        DiaryCheckEntity newCheckDiary = DiaryCheckEntity.builder()
                .id(diaryCheck.getId())
                .userEntity(user)
                .potEntity(pot)
                .bug(diaryDTO.getBug()!=null && diaryDTO.getBug()?diaryDTO.getBug():diaryCheck.getBug())
                .sun(diaryDTO.getSun()!=null && diaryDTO.getSun()?diaryDTO.getSun():diaryCheck.getSun())
                .pruning(diaryDTO.getPruning()!=null && diaryDTO.getPruning() ?diaryDTO.getPruning():diaryCheck.getPruning())
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():diaryCheck.getContent())
                .imgPath(storedFileName)
                .water(diaryDTO.getWater()!=null && diaryDTO.getWater()?diaryDTO.getWater():diaryCheck.getWater())
                .nutrients(diaryDTO.getNutrients()!=null && diaryDTO.getNutrients()?diaryDTO.getNutrients():diaryCheck.getNutrients())
                .build();

        // 다이어리 저장
        DiaryEntity diary = DiaryEntity.builder()
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():false)
                .potEntity(pot)
                .userEntity(user)
                .diaryCheckEntity(newCheckDiary)
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():false)
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():null)
                .imgPath(storedFileName)
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():false)
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():false)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():false)
                .isPotLast(true)
                .isUserLast(true)
                .build();
        diaryRepository.updateIsLastByPotId(pot.getId(), LocalDateTime.now());
        diaryRepository.updateIsLastByUserId(user.getId(), LocalDateTime.now());
        log.info("result: "+newCheckDiary.getId());


        // 물주기 일정 추가
        if(diary.getWater()) {
            addDonePlan(user, pot, 0, diary);
        }
        //영양제 일정 추가
        if(diary.getNutrients()) {
            addDonePlan(user, pot, 1, diary);
        }

        // 점수 계산
        Integer score = 0;
        int[] checkList = {diary.getWater()&&!diaryCheck.getWater()?10:0, diary.getSun()&&!diaryCheck.getSun()?10:0, diary.getPruning()&&!diaryCheck.getPruning()?30:0, diary.getNutrients()&&!diaryCheck.getNutrients()?30:0, diary.getBug()?10:0, diary.getContent()!=null?10:0, image!=null?10:0};
        for(int i: checkList){
            score += i;
        }
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        if(tempExp>=pot.getLevel()*10){
            tempExp -= pot.getLevel()*10;
            tempLevel+=1;
        }else if(tempExp < 0){
            tempLevel -= 1;
            tempExp += tempLevel*10;
        }
        LocalDateTime now = LocalDateTime.now();
        PotEntity newPot = PotEntity.builder()
                .id(pot.getId())
                .humidity(pot.getHumidity())
                .illuminance(pot.getIlluminance())
                .imgPath(pot.getImgPath())
                .name(pot.getName())
                .userEntity(user)
                .diaryEntities(pot.getDiaryEntities())
                .plantId(pot.getPlantId())
                .plantKrName(pot.getPlantKrName())
                .temperature(pot.getTemperature())
                .share(pot.getShare())
                .nutrientsDate(diary.getNutrients()?now:pot.getNutrientsDate())
                .pruningDate(diary.getPruning()?now:pot.getPruningDate())
                .saleDate(pot.getSaleDate())
                .level(tempLevel)
                .experience(tempExp)
                .waterDate(diary.getWater()?now:pot.getWaterDate())
                .survival(pot.getSurvival())
                .plantEntity(pot.getPlantEntity())
                .planEntities(pot.getPlanEntities())
                .build();
//        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
        potRepository.save(newPot);
        diaryCheckRepository.save(newCheckDiary);
        return diaryRepository.save(diary);
    }

    @Override
    public DiaryEntity updateDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
        DiaryEntity diaryEntity = diaryRepository.findById(diaryDTO.getId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
        DiaryCheckEntity diaryCheck = diaryCheckRepository.findById(diaryDTO.getDiaryId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "당일 다이어리 정보를 찾을 수 없습니다."));
        String storedFileName = null;
        if(diaryEntity.getImgPath()!=null) {
            s3Service.delete(diaryEntity.getImgPath());
        }
        if(image != null){
            storedFileName = s3Service.upload(image, "diary");
        }
        PotEntity pot = diaryEntity.getPotEntity();
        // null이면 원래 entity값 가져가고 아니면 추가
        DiaryEntity newDiary = DiaryEntity.builder()
                .id(diaryEntity.getId())
                .userEntity(diaryEntity.getUserEntity())
                .diaryCheckEntity(diaryEntity.getDiaryCheckEntity())
                .potEntity(pot)
                .diaryCheckEntity(diaryCheck)
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():diaryEntity.getBug())
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():diaryEntity.getSun())
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():diaryEntity.getPruning())
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():diaryEntity.getContent())
                .imgPath(storedFileName)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():diaryEntity.getWater())
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():diaryEntity.getNutrients())
                .build();

        DiaryCheckEntity newCheckDiary = DiaryCheckEntity.builder()
                .id(diaryCheck.getId())
                .userEntity(diaryCheck.getUserEntity())
                .potEntity(pot)
                .bug(diaryDTO.getBug()!=null?diaryDTO.getBug():diaryCheck.getBug())
                .sun(diaryDTO.getSun()!=null?diaryDTO.getSun():diaryCheck.getSun())
                .pruning(diaryDTO.getPruning()!=null?diaryDTO.getPruning():diaryCheck.getPruning())
                .content(diaryDTO.getContent()!=null?diaryDTO.getContent():diaryCheck.getContent())
                .imgPath(storedFileName)
                .water(diaryDTO.getWater()!=null?diaryDTO.getWater():diaryCheck.getWater())
                .nutrients(diaryDTO.getNutrients()!=null?diaryDTO.getNutrients():diaryCheck.getNutrients())
                .build();

        //plan 수정
        // 수행 했던 미션을 취소할 때
        if(diaryEntity.getWater() && !newDiary.getWater()) {
            // 작성된 plan 삭제
            planRepository.deleteByCodeAndPotId(pot.getId(), 0, diaryEntity.getCreatedDate());
            // 실행 날짜 업데이트
            LocalDateTime date = planRepository.findLastDateTimeByDoneAndPotIdAndCode(true, pot.getId(), 0);
            log.info("plan에 미션 완료 표시");
            addPlan(diaryEntity.getUserEntity(), pot, 0, date);
        }
        //영양제 일정 추가
        if(diaryEntity.getNutrients() && !newDiary.getNutrients()) {
            // 작성된 plan 삭제
            planRepository.deleteByCodeAndPotId(pot.getId(), 1, diaryEntity.getCreatedDate());
            // 실행 날짜 업데이트
            LocalDateTime date = planRepository.findLastDateTimeByDoneAndPotIdAndCode(true, pot.getId(), 1);
            log.info("plan에 미션 완료 표시");
            addPlan(diaryEntity.getUserEntity(), pot, 1, date);
        }


        // 새로운 미션을 수행했을 때
        UserEntity user = diaryEntity.getUserEntity();

        if(!diaryEntity.getWater() && newDiary.getWater()) {
            addDonePlan(user, pot, 0, newDiary);
        }
        //영양제 일정 추가
        if(!diaryEntity.getNutrients() && newDiary.getNutrients()) {
            addDonePlan(user, pot, 1, newDiary);
        }
//        LocalDateTime now = LocalDateTime.now();
//        if(!diaryEntity.getWater() && newDiary.getWater()) {
//            // 실행 날짜 업데이트
//            if(planRepository.existsByDateTimeAndCode(LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 9, 0, 0), 0)){
//                planRepository.updateDoneAndDateTimeByCodeAndPotId(0, pot.getId());
//            }
//            LocalDateTime date = planRepository.findLastDateTimeByDoneAndPotIdAndCode(true, pot.getId(), 0);
//            log.info("plan에 미션 완료 표시");
//            addPlan(diaryEntity.getUserEntity(), pot, 0, now);
//        }
//        //영양제 일정 추가
//        if(!diaryEntity.getNutrients() && newDiary.getNutrients()) {
//            if(planRepository.existsByDateTimeAndCode(LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 9, 0, 0), 1)){
//                planRepository.updateDoneAndDateTimeByCodeAndPotId(1, pot.getId());
//            }
//            log.info("plan에 미션 완료 표시");
//            // 실행 날짜 업데이트
//            LocalDateTime date = planRepository.findLastDateTimeByDoneAndPotIdAndCode(true, pot.getId(), 1);
//
//            addPlan(diaryEntity.getUserEntity(), pot, 1, now);
//        }


        // 경험치 점수 계산
        Integer score = 0;

        // 이전에 얻었다가 빼야할 점수 계산
        int[] beforeScore = {diaryEntity.getWater() && !newDiary.getWater() ? -10:0,
                diaryEntity.getSun() && !newDiary.getSun() ? -10:0,
                diaryEntity.getPruning() && !newDiary.getPruning() ? -30:0,
                diaryEntity.getNutrients() && !newDiary.getNutrients() ? -30:0,
                diaryEntity.getBug() && !newDiary.getBug() ? -10:0,
                diaryEntity.getContent()!=null && newDiary.getContent()==null? -10:0};
        for(int i: beforeScore){
            score += i;
        }

        // 추가된 점수 계산
        int[] checkList = {newDiary.getWater()?10:0, newDiary.getSun()?10:0, newDiary.getPruning()?30:0, newDiary.getNutrients()?30:0, newDiary.getBug()?10:0, newDiary.getContent()!=null?10:0, image!=null?10:0};
        for(int i: checkList){
            score += i;
        }

        // 경험치 및 레벨 계산
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        if(tempExp>=pot.getLevel()*10){
            tempExp -= pot.getLevel()*10;
            tempLevel+=1;
        }else if(tempExp < 0){
            tempLevel -= 1;
            tempExp += tempLevel*10;
        }

        // 화분 경험치 및 레벨 업데이트
        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);

        diaryCheckRepository.save(newCheckDiary);

        return diaryRepository.save(newDiary);
    }

    @Override
    public Boolean deleteDiary(Long diaryId) {
        if(diaryRepository.existsById(diaryId)){
            DiaryEntity diaryEntity = diaryRepository.findById(diaryId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
            if(diaryEntity.getImgPath()!=null && s3Service.delete(diaryEntity.getImgPath())>0) return false;
            PotEntity pot = diaryEntity.getPotEntity();
            // 다이어리 삭제

            log.info(""+diaryEntity.getDiaryId());
            DiaryCheckEntity diaryCheck = diaryCheckRepository.findById(diaryEntity.getDiaryId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리Check를 찾을 수 없습니다."));
            diaryRepository.deleteById(diaryId);
            // check 테이블 수정
            DiaryCheckEntity newCheckDiary = DiaryCheckEntity.builder()
                    .id(diaryEntity.getDiaryId())
                    .userEntity(diaryEntity.getUserEntity())
                    .potEntity(diaryEntity.getPotEntity())
                    .bug(diaryEntity.getBug()?!diaryEntity.getBug():diaryCheck.getBug())
                    .sun(diaryEntity.getSun()?!diaryEntity.getSun():diaryCheck.getSun())
                    .pruning(diaryEntity.getPruning()?!diaryEntity.getPruning():diaryCheck.getPruning())
                    .content(diaryEntity.getContent()!=null?null:diaryCheck.getContent())
                    .water(diaryEntity.getWater()?!diaryEntity.getWater():diaryCheck.getWater())
                    .nutrients(diaryEntity.getNutrients()?!diaryEntity.getNutrients():diaryCheck.getNutrients())
                    .build();
            DiaryCheckEntity result = diaryCheckRepository.save(newCheckDiary);

            boolean isWater = diaryEntity.getWater();
            boolean isNutrients = diaryEntity.getNutrients();

            if(isWater) {
                // 마지막으로 실행한 날짜 가져오기
                LocalDateTime date = planRepository.findLastDateTimeByDoneAndPotIdAndCode(true, pot.getId(), 0);
                log.info("plan done check water");
                addPlan(diaryEntity.getUserEntity(), pot, 0, date);
            }

            //영양제 일정 추가
            if(isNutrients) {
                // 마지막으로 실행한 날짜 가져오기
                LocalDateTime date = planRepository.findLastDateTimeByDoneAndPotIdAndCode(true, pot.getId(), 1);
                log.info("plan done check nutrients");
                addPlan(diaryEntity.getUserEntity(), pot, 1, date);
            }

            // 점수 갱신
            Integer score = 0;
            int[] checkList = {diaryEntity.getWater()?-10:0, diaryEntity.getSun()?-10:0, diaryEntity.getPruning()?-30:0, diaryEntity.getNutrients()?-30:0, diaryEntity.getBug()?-10:0, diaryEntity.getContent()!=null ?-10:0};
            for(int i: checkList){
                score += i;
            }
            int tempExp = pot.getExperience()+score;
            int tempLevel = pot.getLevel();
            if(tempExp>pot.getLevel()*10){
                tempExp -= pot.getLevel()*10;
                tempLevel+=1;
            }else if(tempExp < 0){
                tempLevel -= 1;
                tempExp += tempLevel*10;
            }
            // 화분 경험치 및 레벨 업데이트
            potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
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
    public DiaryCheckEntity checkDiary(Long diaryId) {
        DiaryCheckEntity diaryCheck = diaryCheckRepository.findById(diaryId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));

        return null;
    }


    @Override
    public Page<DiaryResponseDTO> diaryList(Long userId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<DiaryEntity> diaryEntities = diaryRepository.findAllByUserPK(userId, pageRequest);
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
    public List<PlanDTO> weeklyDiaries(Long userId, String[] dates) {
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        log.info("year: "+year);
        log.info("month: "+month);
        log.info("day: "+day);
        LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59, 59);
        // 해당날짜에 해당하는 plan들 가져오기
        List<PlanEntity> plans = planRepository.findAllByDateTimeAndUserPK(start, end, userId);
        List<PlanDTO> result = new PlanEntity().toPlanDTOList(plans);
        return result;
    }

    private void addPlan(UserEntity user, PotEntity pot, Integer code, LocalDateTime start){

        // 이후의 미션 중 false 인 미션 삭제
        planRepository.deleteAllByCodeAndPotId(code, pot.getId());
        log.info("미션 중 false인 미션 삭제");
//        LocalDateTime now = LocalDateTime.now();
        // 새로운 미션 추가
        List<PlanEntity> planList = new ArrayList<>();
        int day = start.getDayOfMonth();
        int month = start.getMonthValue();
        int year = start.getYear();
        int waterCyclenum = plantRepository.findById(pot.getPlantId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 식물을 찾을 수 없습니다.")).getWaterCycle();
        int waterCycle = PlantCodeUtil.waterCycle[waterCyclenum%53000];
        for (int i = 0; i < 3; i++) {
            if(code==0){
                day += waterCycle;
            }else{
                month += 6;
                if (month > 12) {
                    year += month / 12;
                    month %= 12;
                }
            }
            while (day > monthDate[month]) {
                day -= monthDate[month];
                month += 1;
                if (month > 12) {
                    year += month / 12;
                    month %= 12;
                }
                log.info("day "+day);
            }


//            LocalDateTime newDate = LocalDateTime.of(LocalDate.from(code==1?start.plusMonths(6):start.plusDays(waterCycle)), LocalTime.of(9, 0, 0));
            LocalDateTime newDate = LocalDateTime.of(year, month, day, 9, 0, 0);
            PlanEntity newOne = PlanEntity.builder()
                    .userEntity(user)
                    .potEntity(pot)
                    .dateTime(newDate)
                    .code(code)
                    .done(false)
                    .build();
//            planList.add(newOne);
            PlanEntity result = planRepository.save(newOne);
            if(result != null) {
                log.info("plan 저장 " + i + "번째 완료 " +result.getId());
            }
        }
//        planRepository.saveAll(planList);
    }

    private void addDonePlan(UserEntity user, PotEntity pot, Integer code, DiaryEntity diary){
//        LocalDateTime start = LocalDateTime.of(LocalDate.from(LocalDateTime.now()), LocalTime.of(0, 0, 0));
//        LocalDateTime end = LocalDateTime.of(LocalDate.from(LocalDateTime.now()), LocalTime.of(23, 59, 59));
//        PlanEntity plan = planRepository.findTop1ByCodeAndCreatedDateBetweenOrderByCreatedDateDesc(code, start, end);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = LocalDateTime.of(LocalDate.from(now), LocalTime.of(9, 0, 0));
        if(planRepository.existsByDateTimeAndCode(date, code)){
            planRepository.updateDoneAndDateTimeByCodeAndPotId(code, pot.getId());
        }else{
            PlanEntity plan = PlanEntity.builder()
                    .done(true)
                    .potEntity(pot)
                    .userEntity(user)
                    .code(code)
                    .dateTime(date)
                    .diaryEntity(diary)
                    .build();
            planRepository.save(plan);
        }
        // 해당 미션 완료 표시 및 실행 날짜 업데이트
//        planRepository.updateDoneAndDateTimeByCodeAndPotId(code, pot.getId());
        log.info("plan에 미션 완료 표시");
        addPlan(user, pot, code, LocalDateTime.now());
    }
}
