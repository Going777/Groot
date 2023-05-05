package com.groot.backend.controller;


import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.response.PotDetailDTO;
import com.groot.backend.dto.response.PotListDTO;
import com.groot.backend.service.PotService;
import com.groot.backend.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/pots")
@Tag(name="[POT] Pot API")
@RequiredArgsConstructor
@Slf4j
public class PotController {

    private final PotService potService;
    private final Logger logger = LoggerFactory.getLogger(PotController.class);

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create pot", description = "returns potId")
    public ResponseEntity<Map<String, Object>> createPot(HttpServletRequest request,
                                    @RequestPart("img") @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) MultipartFile multipartFile,
                                    @RequestPart("pot") @Valid @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) PotRegisterDTO potRegisterDTO) {
        logger.info("Create pot : {}", potRegisterDTO.getPotName());
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        try {
            Long ret = potService.createPot(userPK, potRegisterDTO, multipartFile);
            status = HttpStatus.CREATED;
            result.put("msg", "화분이 등록되었습니다.");
            result.put("potId", ret);
        } catch (IOException e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            result.put("msg", "파일 업로드 실패.");
        } catch (NoSuchElementException e) {
            status = HttpStatus.NOT_FOUND;
            result.put("msg", "사용자 또는 식물을 찾을 수 없습니다.");
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            result.put("msg", "DB 저장에 실패했습니다.");
        }

        return new ResponseEntity(result, status);
    }

    @GetMapping("")
    @Operation(summary = "Get list of pot", description = "")
    public ResponseEntity<Map<String, Object>> potList(HttpServletRequest request) {

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        logger.info("Get pot list of user : {}", userPK);
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            List<PotListDTO> list = potService.potList(userPK);
            status = HttpStatus.OK;
            result.put("pots", list);
            result.put("msg", "화분 목록 조회에 성공했습니다.");

        } catch (NoSuchElementException e) {
            logger.info("Failed to load pot list");
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity<>(result, status);
    }

}
