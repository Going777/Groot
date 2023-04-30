package com.groot.backend.controller;


import com.groot.backend.dto.request.PotDTO;
import com.groot.backend.service.PotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pots")
@Tag(name="[POT] Pot API")
@RequiredArgsConstructor
@Slf4j
public class PotController {

    private final PotService potService;
    private final Logger logger = LoggerFactory.getLogger(PotController.class);

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create pot", description = "returns potId")
    public ResponseEntity createPot(@RequestPart MultipartFile img, @RequestPart PotDTO potDTO) {
        logger.info("Create pot : {}", potDTO.getPotName());
        Map<String, Object> result = new HashMap<>();

        Long ret = potService.createPot(potDTO, img);

        if(ret < 0) {
            result.put("msg", "화분 등록에 실패했습니다.");
            return new ResponseEntity(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        result.put("msg", "화분이 등록되었습니다.");
        result.put("potId", ret);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }
}
