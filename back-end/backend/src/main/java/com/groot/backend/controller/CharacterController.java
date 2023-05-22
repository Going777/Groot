package com.groot.backend.controller;

import com.google.api.Http;
import com.groot.backend.dto.response.CharacterCollectionDTO;
import com.groot.backend.dto.response.CharacterImageDTO;
import com.groot.backend.service.CharacterService;
import com.groot.backend.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/characters")
@Tag(name="[Character] Character API")
@RequiredArgsConstructor
@Slf4j
public class CharacterController {

    private final Logger logger = LoggerFactory.getLogger(CharacterController.class);

    private final CharacterService characterService;

    @GetMapping("/images")
    public ResponseEntity<Map<String, Object>> getImageList() {
        logger.info("Get character image list");
        HttpStatus status;
        Map<String, Object> result = new HashMap<>();

        try {
            List<CharacterImageDTO> list = characterService.getImageList();
            result.put("msg", "캐릭터 이미지 리스트 조회에 성공했습니다.");
            result.put("characters", list);
            status = HttpStatus.OK;
        } catch (NoSuchElementException e) {
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(result, status);
    }

    @GetMapping("/collections")
    public ResponseEntity<Map<String, Object>> getCollections(HttpServletRequest request) {
        logger.info("Get collection list");

        Long userPK;
        try {
            userPK = JwtTokenProvider.getIdByAccessToken(request);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            logger.info("Failed to parse token : {}", request.getHeader("Authorization"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        HttpStatus status;
        Map<String, Object> result = new HashMap<>();

        try {
            List<Integer> list = characterService.getCollections(userPK);
            result.put("msg", "도감 조회에 성공했습니다.");
            result.put("positions", list);
            status = HttpStatus.OK;
        } catch (NoSuchElementException e) {
            result.put("msg", "등록된 화분이 없습니다.");
            status = HttpStatus.NOT_FOUND;
        } catch (Exception e) {
            logger.info("error : {}", e.getStackTrace());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

//        result.put("msg", "도감 조회에 성공했습니다.");
//        result.put("positions", new int[] {0, 1, 2, 13, 21});
//        status = HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }
}
