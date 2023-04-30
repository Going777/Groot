package com.groot.backend.service;

import com.groot.backend.controller.NotificationController;
import com.groot.backend.controller.exception.CustomException;
import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.dto.response.DiaryResponseDTO;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.repository.DiaryRepository;
//import com.groot.backend.util.S3Uploader;
import com.groot.backend.repository.PotRepository;
import com.groot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

//    @Autowired
//    private S3Uploader s3Uploader;


    @Transactional
    @Override
    public DiaryEntity saveDiary(Long userId, MultipartFile image, DiaryDTO diaryDTO) throws IOException {
        String storedFileName = null;
        if(image==null){
//            storedFileName = s3Uploader.upload(image, "images");
        }
        log.info("PK"+userId);
        DiaryEntity diary = DiaryEntity.builder()
                .bug(diaryDTO.getBug()?true:false)
//                .potEntity(potRepository.findById(diaryDTO.getPotId()).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 화분을 찾을 수 없습니다.")))
                .userEntity(userRepository.findById(userId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.")))
                .sun(diaryDTO.getSun()?true:false)
                .content(diaryDTO.getContent())
                .imgPath(storedFileName)
                .nutrients(diaryDTO.getNutrients()?true:false)
                .pruning(diaryDTO.getPruning()?true:false)
                .water(diaryDTO.getWater()?true:false)
                .build();
//        List<Long> subsToList = subscribeRepository.findSubscribeTo(principalDetails.getUser().getId());
        Long id = userId;
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
//        DiaryEntity diaryEntity = null;
        String storedFileName = null;
        if(image==null){
//            storedFileName = s3Uploader.upload(image, "images");
        }
        DiaryEntity newDiary = DiaryEntity.builder()
                .id(diaryEntity.getId())
                .userEntity(diaryEntity.getUserEntity())
                .potEntity(diaryEntity.getPotEntity())
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
    public Boolean deleteDiary(Long diaryId) {
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

    @Override
    public Page<DiaryResponseDTO> diaryListByPotId(Long potId, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<DiaryEntity> diaryEntities = diaryRepository.findAllByPotId(potId, pageRequest);
        Page<DiaryResponseDTO> result = new DiaryResponseDTO().toDtoList(diaryEntities);
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
