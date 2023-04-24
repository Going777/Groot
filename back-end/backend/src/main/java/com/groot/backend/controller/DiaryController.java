package com.groot.backend.controller;

import com.groot.backend.dto.request.DiaryDTO;
import com.groot.backend.entity.DiaryEntity;
import com.groot.backend.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/diaries")
@RestController
@RequiredArgsConstructor
@Slf4j
public class DiaryController {

    private final DiaryService diaryService;
    @PostMapping()
    public ResponseEntity insertDiary(@RequestPart("postData") @Validated DiaryDTO diaryDTO, @RequestPart(value="files", required = false) MultipartFile file, HttpServletRequest request) throws Exception {
        Map resultMap = new HashMap();
        Long userId = 1L;

        if(diaryDTO.getBug() && diaryDTO.getNutrients() && diaryDTO.getPruning() && diaryDTO.getWater() && diaryDTO.getSun() && diaryDTO.getContent().isEmpty()){
            resultMap.put("msg", "다이어리에 추가할 내용이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(diaryService.saveDiary(userId, file, diaryDTO)==null){
            resultMap.put("msg", "다이어리 작성 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
        resultMap.put("msg","다이어리 작성 완료");
        return ResponseEntity.ok().body(resultMap);

    }

    @PutMapping()
    public ResponseEntity updateDiary(@RequestPart("postData") @Validated DiaryDTO diaryDTO, @RequestPart(value="files", required = false) MultipartFile file, HttpServletRequest request) throws Exception {
        Map resultMap = new HashMap();
        Long userId = 1L;

        if(diaryDTO.getUserPK()!=userId){
            resultMap.put("msg", "수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(diaryDTO.getBug() && diaryDTO.getNutrients() && diaryDTO.getPruning() && diaryDTO.getWater() && diaryDTO.getSun() && diaryDTO.getContent().isEmpty()){
            resultMap.put("msg", "다이어리를 수정할 내용이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(diaryService.updateDiary(userId, file, diaryDTO)==null){
            resultMap.put("msg", "다이어리 수정 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
        resultMap.put("msg","다이어리 수정 완료");
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/{diaryId}/{userPK}")
    public ResponseEntity deleteDiary(@PathVariable Long diaryId, @PathVariable Long userPK,HttpRequest request){
        Map resultMap = new HashMap();
        Long userId = 1L;
        if(userId != userPK){
            resultMap.put("msg", "삭제 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        if(diaryService.deleteDiary(userPK, diaryId)){
            resultMap.put("msg", "다이어리 삭제 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("다이어리 삭제 실패");
        }
        resultMap.put("msg", "다이어리 삭제 성공");
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity detailDiary(@PathVariable Long diaryId){
        Map resultMap = new HashMap();
        DiaryEntity result = diaryService.detailDiary(diaryId);
        if(result == null){
            resultMap.put("msg", "다이어리 조회에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
        }
        resultMap.put("msg", "다이어리 조회에 성공하였습니다.");
        resultMap.put("diary", result);
        return ResponseEntity.ok().body(resultMap);
    }
}
