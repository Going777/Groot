package com.groot.backend.controller;

import com.groot.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plans")
@Slf4j
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
}
