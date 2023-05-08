package com.groot.backend.service;

import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.dto.response.DiaryResponseDTO;
import com.groot.backend.dto.response.PlanDTO;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
//import com.groot.backend.util.S3Uploader;
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

    private static int[] monthDate = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    @Override
    public DiaryDTO isExistByCreatedDate(Long potId) {
        return new DiaryCheckEntity().toDTO(diaryCheckRepository.existsByPotIdCreatedDate(potId));
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
        DiaryCheckEntity result = diaryCheckRepository.save(checkdiary);

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
                .build();

        log.info("result: "+result.getId());

        // 물주기 일정 추가
        if(diary.getWater()) addPlan(user, pot, 0);
        //영양제 일정 추가
        if(diary.getNutrients()) addPlan(user, pot, 1);

        // 화분 경험치 계산
        int[] checkList = {diaryDTO.getWater()!=null?10:0, diaryDTO.getSun()!=null?10:0, diaryDTO.getPruning()!=null?30:0, diaryDTO.getNutrients()!=null?30:0, diaryDTO.getBug()!=null?10:0, diaryDTO.getContent()!=null?10:0, image!=null?10:0};
        for(int i: checkList){
            score += i;
        }
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        if(tempExp>=tempLevel*100){  // 경험치는 해당 레벨의 x100을 얻어야 레벨업 가능
            tempExp -= tempLevel*100;
            tempLevel+=1;
        }
        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);

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
                .build();
        log.info("result: "+newCheckDiary.getId());

        // 물주기 일정 추가
        if(diary.getWater()) addPlan(user, pot, 0);
        //영양제 일정 추가
        if(diary.getNutrients()) addPlan(user, pot, 1);

        // 점수 계산
        Integer score = 0;
        int[] checkList = {diary.getWater()&&!diaryCheck.getWater()?10:0, diary.getSun()&&!diaryCheck.getSun()?10:0, diary.getPruning()&&!diaryCheck.getPruning()?30:0, diary.getNutrients()&&!diaryCheck.getNutrients()?30:0, diary.getBug()?10:0, diary.getContent()!=null?10:0, image!=null?10:0};
        for(int i: checkList){
            score += i;
        }
        int tempExp = pot.getExperience()+score;
        int tempLevel = pot.getLevel();
        if(tempExp>=pot.getLevel()*100){
            tempExp -= pot.getLevel()*100;
            tempLevel+=1;
        }else if(tempExp < 0){
            tempLevel -= 1;
            tempExp += tempLevel*100;
        }
        potRepository.updateExpLevelById(pot.getId(), tempExp, tempLevel);
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
        if(tempExp>=pot.getLevel()*100){
            tempExp -= pot.getLevel()*100;
            tempLevel+=1;
        }else if(tempExp < 0){
            tempLevel -= 1;
            tempExp += tempLevel*100;
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
            diaryRepository.deleteById(diaryId);
            DiaryCheckEntity diaryCheck = diaryCheckRepository.findById(diaryEntity.getDiaryId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 다이어리를 찾을 수 없습니다."));
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
            // 점수 갱신
            Integer score = 0;
            int[] checkList = {diaryEntity.getWater()?-10:0, diaryEntity.getSun()?-10:0, diaryEntity.getPruning()?-30:0, diaryEntity.getNutrients()?-30:0, diaryEntity.getBug()?-10:0, diaryEntity.getContent()!=null ?-10:0};
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
    public List<PlanDTO> weeklyDiaries(Long userId, LocalDateTime start, LocalDateTime end) {
        // 해당날짜에 해당하는 plan들 가져오기
        List<PlanEntity> plans = planRepository.findAllByDateTime(start, end);
        List<PlanDTO> result = new PlanEntity().toPlanDTOList(plans);
        return result;
    }

    private void addPlan(UserEntity user, PotEntity pot, Integer code){

        // 해당 미션 완료 표시 및 실행 날짜 업데이트
        planRepository.updateDoneAndDateTimeByCodeAndPotId(code, pot.getId());
        // 이후의 미션 중 false 인 미션 삭제
        planRepository.deleteAllByCodeAndPotId(code, pot.getId());
        LocalDateTime now = LocalDateTime.now();
        int addPoint = 6;
        if(code==0){
            log.info("plant"+pot.getPlantId());
            addPoint = plantRepository.findById(pot.getPlantId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 식물을 찾을 수 없습니다.")).getWaterCycle();

        }
        // 새로운 미션 추가
        List<PlanEntity> planList = new ArrayList<>();
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();
        for (int i = 0; i < 3; i++) {
            day += addPoint;
            if (day > monthDate[month]) {
                day -= monthDate[month];
                month += 1;
                if (month > 12) {
                    year += month / 12;
                    month %= 12;
                }
            }
            LocalDateTime newDate = LocalDateTime.of(year, month, day, 0, 0, 0);
            PlanEntity newOne = PlanEntity.builder()
                    .userEntity(user)
                    .potEntity(pot)
                    .dateTime(newDate)
                    .code(code)
                    .build();
//            planList.add(newOne);
            planRepository.save(newOne);
        }
//        planRepository.saveAll(planList);
    }

}
