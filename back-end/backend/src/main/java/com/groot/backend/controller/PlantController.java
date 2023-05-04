package com.groot.backend.controller;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.dto.response.PlantThumbnailDTO;
import com.groot.backend.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/plants")
@Tag(name="[PLANT] Plant API")
@RequiredArgsConstructor
@Slf4j
public class PlantController {
    private final Logger logger = LoggerFactory.getLogger(PlantController.class);
    private final PlantService plantService;
    @Value("${plantnet.apiKey}")
    private String plantNetApiKey;
    @Value("${plant.temp.dir}")
    private String plantTempDir;

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

    @GetMapping("/{plantId}")
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

    @GetMapping()
    @Operation(summary = "Get plant thumbnail", description = "")
    public ResponseEntity<Map<String, Object>> plantList(PlantSearchDTO plantSearchDTO) {
        logger.info("Get plant list : {}", plantSearchDTO);
        logger.info("param : name : {}", plantSearchDTO.getName());
        logger.info("param : diff : {}", plantSearchDTO.getDifficulty());
        logger.info("param : lux : {}", plantSearchDTO.getLux());
        logger.info("param : growth : {}", plantSearchDTO.getGrowth());
        logger.info("page no : {}", plantSearchDTO.getPage());

        Map<String, Object> result = new HashMap<>();
        List<PlantThumbnailDTO> list = plantService.plantList(plantSearchDTO);

        if(list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        result.put("msg", "식물 목록 조회에 성공했습니다.");
        result.put("plants", list);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/identify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Get plant info by photo", description = "")
    public ResponseEntity<Map<String, Object>> identifyPlant(@RequestPart("file") MultipartFile multipartFile) {
        logger.info("Identify plant : {}", multipartFile.getOriginalFilename());
        logger.info("API Key : {}", plantNetApiKey);
        logger.info("File directory : {}", plantTempDir);
        Map<String, Object> result = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> reqBody = new LinkedMultiValueMap<>();

        File retFile = new File(plantTempDir + multipartFile.getOriginalFilename());

        try {
            if(retFile.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(retFile);
                fos.write(multipartFile.getBytes());
                fos.close();
            }
        } catch (IOException e) {
            logger.info("Failed to create file");
            result.put("msg", "Failed to convert image file");
            retFile.delete();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        reqBody.add("images", new FileSystemResource(plantTempDir + retFile.getName()));

        String url = "https://my-api.plantnet.org/v2/identify/all?include-related-images=false&no-reject=false&lang=en&api-key=" + plantNetApiKey;
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(reqBody, headers);

        // send request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<HashMap> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, HashMap.class);

        // if success & fail
        logger.info("response : {}", response.getStatusCode());
        logger.info("body : {}", response.getBody().toString());

        // delete local file
        logger.info("Delete file : {}", retFile.delete());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
