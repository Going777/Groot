package com.groot.backend.controller;

import com.groot.backend.service.PlanService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/plans")
@Slf4j
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;

    private static String SUCCESS = "success";
    private static String FAIL = "fail";

    @DeleteMapping("/{planId}/{userPK}")
    public ResponseEntity deletePlan(@PathVariable Long planId, @PathVariable Long userPK, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(userId != userPK){
            resultMap.put("msg", "삭제 권한이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
        }
        planService.deletePlan(planId);
//        if(!planService.deletePlan(planId)){
//            resultMap.put("msg", "다이어리 삭제 실패");
//            resultMap.put("result", FAIL);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("다이어리 삭제 실패");
//        }
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "다이어리 삭제 성공");
        return ResponseEntity.ok().body(resultMap);
    }
}
