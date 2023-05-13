package com.groot.backend.controller;

import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.dto.response.DiaryResponseDTO;
import com.groot.backend.dto.response.PlanDTO;
import com.groot.backend.entity.DiaryCheckEntity;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.service.DiaryService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@RequestMapping("/diaries")
@RestController
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;
    private static String SUCCESS = "success";
    private static String FAIL = "fail";

    @PostMapping    // 다이어리 작성
    public ResponseEntity insertDiary(@RequestPart("postData") @Validated DiaryDTO diaryDTO, @RequestPart(value="image", required = false) MultipartFile file, HttpServletRequest request) throws Exception {
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(diaryDTO.getBug()==null && diaryDTO.getNutrients()==null && diaryDTO.getPruning()==null && diaryDTO.getWater()==null && diaryDTO.getSun()==null && diaryDTO.getContent()==null){
            resultMap.put("msg", "다이어리에 추가할 내용이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
//        Date now = new Date();
//        SimpleDateFormat sDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String nowFormat = sDate.format(now);
        // 오늘 다이어리를 작성한 이력이 있을 경우
        DiaryCheckEntity result = diaryService.isExistByCreatedDate(diaryDTO.getPotId());

        if(result!=null){
            DiaryDTO find = new DiaryCheckEntity().toDTO(result);
            DiaryEntity diaryUpdate = diaryService.saveAndUpdateDiary(userId, file, diaryDTO, find);
            if(diaryUpdate==null){
                resultMap.put("msg", "다이어리 추가 및 수정 실패");
                resultMap.put("result", FAIL);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
            }
            resultMap.put("msg","다이어리 추가 및 수정 완료");
            resultMap.put("result", SUCCESS);
            return ResponseEntity.ok().body(resultMap);
        }
        DiaryEntity diarySave = diaryService.saveDiary(userId, file, diaryDTO);
        if(diarySave==null){
            resultMap.put("msg", "다이어리 추가 및 작성 실패");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
        resultMap.put("msg","다이어리 추가 및 작성 완료");
        resultMap.put("result", SUCCESS);
        return ResponseEntity.ok().body(resultMap);

    }

    @PutMapping     // 다이어리 수정
    public ResponseEntity updateDiary(@RequestPart("postData") @Validated DiaryDTO diaryDTO, @RequestPart(value="image", required = false) MultipartFile file, HttpServletRequest request) throws Exception {
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);

        if(diaryDTO.getUserPK()!=userId){
            resultMap.put("msg", "수정 권한이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(diaryDTO.getBug()==null && diaryDTO.getNutrients()==null && diaryDTO.getPruning()==null && diaryDTO.getWater()==null && diaryDTO.getSun()==null && diaryDTO.getContent()==null){
            resultMap.put("msg", "다이어리를 수정할 내용이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(diaryService.updateDiary(userId, file, diaryDTO)==null){
            resultMap.put("msg", "다이어리 수정 실패");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
        resultMap.put("msg","다이어리 수정 완료");
        resultMap.put("result", SUCCESS);
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/{diaryId}/{userPK}")    // 다이어리 삭제
    public ResponseEntity deleteDiary(@PathVariable Long diaryId, @PathVariable Long userPK, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(userId != userPK){
            resultMap.put("msg", "삭제 권한이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(!diaryService.deleteDiary(diaryId)){
            resultMap.put("msg", "다이어리 삭제 실패");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("다이어리 삭제 실패");
        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "다이어리 삭제 성공");
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/detail/{diaryId}")   // 다이어리 상세 조회
    public ResponseEntity detailDiary(@PathVariable Long diaryId){
        Map resultMap = new HashMap();
        DiaryResponseDTO result = diaryService.detailDiary(diaryId);
        if(result == null){
            resultMap.put("msg", "다이어리 상세 조회에 실패했습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
        resultMap.put("msg", "다이어리 상세 조회에 성공하였습니다.");
        resultMap.put("diary", result);
        resultMap.put("result", SUCCESS);
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/check/{potId}")
    public ResponseEntity checkDiary(@PathVariable Long potId){
        Map resultMap = new HashMap();
        DiaryCheckEntity result = diaryService.isExistByCreatedDate(potId);
        DiaryDTO diaryDTO = result==null?null:new DiaryCheckEntity().toDTO(result);
        // 없든 있든 결과 보내줌
        resultMap.put("diary", diaryDTO==null?null:diaryDTO);
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "해당 사용자의 다이어리 조회에 성공하였습니다.");
        return ResponseEntity.ok().body(resultMap);
    }


    @GetMapping("/list") // 해당 사용자의 모든 다이어리 리스트
    public ResponseEntity diaryList(@RequestParam Integer page, @RequestParam Integer size, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        Page<DiaryResponseDTO> result = diaryService.diaryList(userId, page, size);
        if(result.isEmpty()){
            resultMap.put("msg", "해당 사용자의 다이어리를 찾을 수 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
        }
        resultMap.put("diary", result);
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "해당 사용자의 다이어리 조회에 성공하였습니다.");
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/{potId}")   // 화분 아이디로 다이어리 목록 조회
    public ResponseEntity potDiary(@PathVariable Long potId, @RequestParam Integer page, @RequestParam Integer size, HttpServletRequest request){
        Map resultMap = new HashMap();
        if(potId==0){
            Long userId = JwtTokenProvider.getIdByAccessToken(request);
            Page<DiaryResponseDTO> result = diaryService.diaryList(userId, page, size);
            if(result.isEmpty()){
                resultMap.put("msg", "해당 사용자의 다이어리를 찾을 수 없습니다.");
                resultMap.put("result", FAIL);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
            }
            resultMap.put("diary", result);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "해당 사용자의 다이어리 조회에 성공하였습니다.");
            return ResponseEntity.ok().body(resultMap);
        }
        Page<DiaryResponseDTO> result = diaryService.diaryListByPotId(potId, page, size);

        if(result.isEmpty()){
            resultMap.put("msg", "해당 화분의 다이어리를 찾을 수 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
        }
        resultMap.put("diary", result);
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "해당 화분의 다이어리 조회에 성공하였습니다.");
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/weekly")  // 주간 다이어리 리스트
    public ResponseEntity weeklyDiary(@RequestParam String date, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        String[] dates = date.split("-");
        if(dates.length<3){
            resultMap.put("msg", "날짜 표현이 유효하지 않습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
        }

        List<PlanDTO> result = diaryService.weeklyDiaries(userId, dates);
        if(result.isEmpty()){
            resultMap.put("msg", "주간 다이어리 리스트 조회 결과가 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMap);
        }
        resultMap.put("diary", result);
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "주간 다이어리 리스트 조회에 성공하였습니다.");
        return ResponseEntity.ok().body(resultMap);
    }
}
