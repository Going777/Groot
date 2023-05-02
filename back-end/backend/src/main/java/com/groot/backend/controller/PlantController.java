package com.groot.backend.controller;

import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plants")
@Tag(name="[PLANT] Plant API")
@RequiredArgsConstructor
@Slf4j
public class PlantController {
    private final Logger logger = LoggerFactory.getLogger(PlantController.class);
    private final PlantService plantService;

    @GetMapping("/names")
    @Operation(summary = "Get plant namne list", description = "no params required")
    public ResponseEntity<Map<String, Object>> getPlantNames() {
        logger.info("Get plant name list");
        Map<String, Object> result = new HashMap<>();

        List<String> nameList = plantService.getNameList();

        logger.info("plant name list : {} found", nameList.size());
        if(nameList.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {
            result.put("msg", "식물 이름 조회에 성공하였습니다.");
            result.put("nameList", nameList);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("{plantId}")
    @Operation(summary = "Get plant detail info", description = "requires platn id")
    public ResponseEntity<Map<String, Object>> plantDetail(@PathVariable Long plantId) {
        logger.info("Get plant detail info : {}", plantId);
        Map<String, Object> result = new HashMap<>();

        PlantDetailDTO plantDetailDTO = plantService.plantDetail(plantId);

        if(plantDetailDTO == null) {
            result.put("msg", "존제하지 않는 식물입니다.");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        else {
            result.put("msg", "식물 정보 조회에 성공하였습니다.");
            result.put("plant", plantDetailDTO);

            return new ResponseEntity<>(result, HttpStatus.OK);

        }
    }


}
