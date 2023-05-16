package com.groot.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceSample {
    private final Logger logger = LoggerFactory.getLogger(FileServiceSample.class);

    private final S3Service s3Service;

    public String upload(MultipartFile multipartFile) throws IOException {
        // 1개 파일 업로드 - request 에서 받은 multipartfile 그대로
        // dirName 은 카테고리에 맞게 설정
        // 파일 URL string 반환
        // 실패시 IOException
        return s3Service.upload(multipartFile, "assets");
    }

    public String[] upload(MultipartFile[] multipartFiles) throws IOException {
        // 파일 여러개 업로드 - MultipartFile 배열
        // dirName 은 카테고리에 맞게 설정
        // 파일 URL string 배열 반환
        // 하나라도 실패시 IOException
        return s3Service.upload(multipartFiles, "assets");
    }

    public int delete(String fileURL) {
        // 파일 한개 삭제 - 파일 URL : https:// 부터 전체
        // 성공 0 반환
        // 잘못된 입력 -1 반환
        return s3Service.delete(fileURL);
    }

    public int delete(List<String> fileURLs) {
        // 파일 여러개 삭제 - 파일 URL list
        // 배열 아님 리스트
        // 성공 0 반환
        // 잘못된 입력 -1 반환
        return s3Service.delete(fileURLs);
    }
}
