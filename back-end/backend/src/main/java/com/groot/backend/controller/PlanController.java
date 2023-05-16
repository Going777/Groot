package com.groot.backend.controller;

import com.groot.backend.service.PlanService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/{planId}/{userPK}")
    public ResponseEntity cancelPlan(@PathVariable Long planId, @PathVariable Long userPK, HttpServletRequest request){
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        Map resultMap = new HashMap<>();
        if(userId!=userPK){
            resultMap.put("result", FAIL);
            resultMap.put("msg", "취소 권한이 없습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }
        planService.deletePlan(planId);
        resultMap.put("result", SUCCESS);
        resultMap.put("msg", "plan 취소를 성공하였습니다.");
        return ResponseEntity.badRequest().body(resultMap);

    }
}
