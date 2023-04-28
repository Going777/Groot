package com.groot.backend.controller;

import com.groot.backend.service.FileServiceSample;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/files")
@RestController
@RequiredArgsConstructor
@Slf4j
public class FileControllerSample {
    private final Logger logger = LoggerFactory.getLogger(FileControllerSample.class);
    private final FileServiceSample fileServiceSample;

    @PostMapping(value = "/one", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity upload(@RequestPart("file") MultipartFile multipartFile) {
        logger.info("Upload file : {}", multipartFile.getName());
        Map<String, Object> result = new HashMap<>();

        try {
            String fileURL = fileServiceSample.upload(multipartFile);
            result.put("URL", fileURL);
            return ResponseEntity.ok().body(result);
        } catch (IOException e) {
            result.put("msg", "FAIL");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @PostMapping(value = "/multi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity uploads(@RequestPart("files") MultipartFile[] multipartFiles) {
        logger.info("Upload {} files", multipartFiles.length);
        Map<String, Object> result = new HashMap<>();

        try {
            String[] fileURLs = fileServiceSample.upload(multipartFiles);
            result.put("URLs", fileURLs);
            return ResponseEntity.ok().body(result);
        } catch (IOException e) {
            result.put("msg", "FAIL");
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping(value = "/one")
    public ResponseEntity delete(@RequestBody String fileURL) {
        logger.info("delete file : {}", fileURL);
        Map<String, Object> result = new HashMap<>();

        int res = fileServiceSample.delete(fileURL);
        if(res == 0) {
            result.put("msg", "SUCCESS");
            return ResponseEntity.ok().body(result);
        }
        else {
            result.put("msg", "FAIL");
            return ResponseEntity.internalServerError().body(result);
        }

    }

    @DeleteMapping(value = "/multi")
    public ResponseEntity deletes(@RequestBody List<String> fileURLs) {
        logger.info("delete {} files", fileURLs.size());
        Map<String, Object> result = new HashMap<>();

        int res = fileServiceSample.delete(fileURLs);

        if(res == 0){
            result.put("msg", "SUCCESS");
            return ResponseEntity.ok().body(result);
        }
        else {
            result.put("msg", "FAIL");
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
