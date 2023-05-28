package com.groot.backend.controller;


import com.google.api.Http;
import com.groot.backend.controller.exception.WrongArticleException;
import com.groot.backend.dto.request.PotModifyDTO;
import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.request.PotTransferDTO;
import com.groot.backend.dto.response.PotDetailDTO;
import com.groot.backend.dto.response.PotListDTO;
import com.groot.backend.dto.response.PotTransferInfoDTO;
import com.groot.backend.service.PotService;
import com.groot.backend.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
                                    @RequestPart(value = "img", required = false) @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) MultipartFile multipartFile,
                                    @RequestPart("pot") @Valid @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) PotRegisterDTO potRegisterDTO) {
        logger.info("Create pot : {}", potRegisterDTO.getPotName());
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
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
    public ResponseEntity<Map<String, Object>> activePotList(HttpServletRequest request) {
        return potList(request, false);
    }

    @GetMapping("/archive")
    @Operation(summary = "Get all the list of pot", description = "")
    public ResponseEntity<Map<String, Object>> potArchive(HttpServletRequest request) {
        return potList(request, true);
    }

    @GetMapping("/{potId}")
    @Operation(summary = "pot detail")
    public ResponseEntity<Map<String, Object>> potDetail(HttpServletRequest request, @PathVariable Long potId) {

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Find pot : {}", potId);
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            PotDetailDTO potDetailDTO = potService.potDetail(userPK, potId);
            result.put("msg", "화분 조회에 성공했습니다.");
            result.put("pot", potDetailDTO.getPot());
            result.put("plant", potDetailDTO.getPlant());
            result.put("waterDate", potDetailDTO.getWaterDate());
            result.put("nutrientsDate", potDetailDTO.getNutrientsDate());
            result.put("pruningDate", potDetailDTO.getPruningDate());
            status = HttpStatus.OK;
        } catch (AccessDeniedException e) {
            status = HttpStatus.FORBIDDEN;
            result.put("msg", "허가되지 않은 접근입니다.");
        } catch (NoSuchElementException e) {
            status = HttpStatus.NOT_FOUND;
            result.put("msg", "화분을 찾을 수 없습니다.");
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            result.put("msg", e.getMessage());
        }

        return new ResponseEntity<>(result, status);
    }

    @PutMapping("/{potId}")
    @Operation(summary = "Modify pot info", description = "Image only")
    public ResponseEntity<Map<String, Object>> modifyPot(
            HttpServletRequest request, @PathVariable Long potId,
            @RequestPart(value = "img", required = false) @Parameter(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) MultipartFile multipartFile,
            @RequestPart(value = "pot", required = false) @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) PotModifyDTO potModifyDTO) {

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        logger.info("modify pot : {}", potId);
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            String imgPath = potService.modifyPot(userPK, potId, potModifyDTO, multipartFile);
            result.put("msg", "화분 정보 변경에 성공했습니다.");
            result.put("img", imgPath);
            status = HttpStatus.OK;
        } catch (AccessDeniedException e) {
            status = HttpStatus.FORBIDDEN;
            result.put("msg", "허가되지 않은 접근입니다.");
        } catch (NoSuchElementException e) {
            status = HttpStatus.NOT_FOUND;
            result.put("msg", "존재하지 않는 화분입니다.");
        } catch (IOException e) {
            result.put("msg", "파일 업로드에 실패했습니다.");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception e) {
            result.put("msg", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    @DeleteMapping("/{potId}")
    @Operation(summary = "Delete pot..", description = "")
    public ResponseEntity<Map<String, Object>> deletePot(HttpServletRequest request, @PathVariable Long potId) {

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Delete pot : {}", potId);
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            int ret = potService.deletePot(userPK, potId);
            result.put("msg", "성공적으로 삭제 되었습니다.");
            status = HttpStatus.OK;
        } catch (AccessDeniedException e) {
            status = HttpStatus.FORBIDDEN;
            result.put("msg", "허가되지 않은 접근입니다.");
        } catch (NoSuchElementException e) {
            result.put("msg", "존재하지 않는 화분입니다.");
            status = HttpStatus.NOT_FOUND;
       } catch (IllegalArgumentException e) {
            result.put("msg", "이미 삭제 된 화분입니다.");
            status = HttpStatus.GONE;
        } catch (Exception e) {
            result.put("msg", e.getStackTrace());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    @PutMapping("/{potId}/status")
    @Operation(summary = "toggle status", description = "resurrection is not implemented yet : 503")
    public ResponseEntity<Map<String, Object>> toggleStatus(HttpServletRequest request, @PathVariable Long potId) {
        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Toggle status : {}", potId);
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            boolean potStatus = potService.toggleStatus(userPK, potId);
            result.put("msg", "상태 변경에 성공했습니다.");
            result.put("status", potStatus);
            status = HttpStatus.OK;
        } catch (AccessDeniedException e) {
            status = HttpStatus.FORBIDDEN;
            result.put("msg", "허가되지 않은 접근입니다.");
        } catch (NoSuchElementException e) {
            result.put("msg", "존재하지 않는 화분입니다.");
            status = HttpStatus.NOT_FOUND;
        } catch (NotYetImplementedException e) {
            result.put("msg", "아직 구현되지 않은 기능입니다.");
            status = HttpStatus.SERVICE_UNAVAILABLE;
        } catch (Exception e) {
            logger.info("ERROR : {}", e.getCause());
            result.put("msg", e.getCause());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    @PostMapping("/transfers")
    @Operation(summary = "create transfer request", description = "pot expires from current user")
    public ResponseEntity<Map<String, Object>> createTransfer(
            HttpServletRequest request,
            @Valid @RequestBody PotTransferDTO potTransferDTO)
    {
        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Create Pot Transfer : {} to {}, {}", userPK, potTransferDTO.getUserPK(), potTransferDTO.getPotId());
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            Long potTransferId = potService.createTransfer(userPK, potTransferDTO);
            result.put("msg", "나눔 요청 생성에 성공했습니다.");
            status = HttpStatus.CREATED;
        } catch (NoSuchElementException e) {
            status = HttpStatus.NOT_FOUND;
        } catch (WrongArticleException | IllegalStateException e) {
            status = HttpStatus.CONFLICT;
        } catch (AccessDeniedException e) {
            status = HttpStatus.FORBIDDEN;
        } catch (Exception e) {
            logger.info("Error : {}", e.getStackTrace());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    @GetMapping("/transfers")
    @Operation(summary = "get list of received transfer requests")
    public ResponseEntity<Map<String, Object>> getTransfers(HttpServletRequest request) {
        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Get received list");
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;
        try {
            List<PotTransferInfoDTO> list = potService.getTransferList(userPK);
            result.put("list", list);
            status = HttpStatus.OK;
        } catch (NoSuchElementException e) {
            logger.info("No transfers found for : {}", userPK);
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            logger.info("Error : {}", e.getStackTrace());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    /**
     * used for both active list and archive
     * @param request
     * @param isArchive
     * @return
     */
    public ResponseEntity<Map<String, Object>> potList(HttpServletRequest request, Boolean isArchive) {

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        logger.info("Get pot list of user : {}", userPK);
        Map<String, Object> result = new HashMap<>();
        HttpStatus status;

        try {
            List<PotListDTO> list = potService.potList(userPK, isArchive);
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
