package com.groot.backend.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {

    private final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Override
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File file = convert(multipartFile)
                .orElseThrow(() -> new IOException("Failed to create file"));

        return uploadFile(file, dirName);
    }

    @Override
    public String[] upload(MultipartFile[] multipartFiles, String dirName) throws IOException {
        int fileLength = multipartFiles.length;
        String[] ret = new String[fileLength];
        int cnt = 0;

        try {
            for(int i=0; i<fileLength; i++) {
                // transactional test
//                if(i == 3) throw new Exception();
                File file = convert(multipartFiles[i])
                        .orElseThrow(() -> new IOException("Failed to create file"));

                ret[i] = uploadFile(file, dirName);
                cnt ++;
            }
        } catch (Exception e) {
            logger.info("Failed to upload file at S3 server");
            for(int i=cnt-1; i>=0; i--) {
                deleteFile(ret[i]);
            }
            throw new IOException("Failed to upload file at S3 server");
        }
        return ret;
    }

    @Override
    public String uploadFile(File file, String dirName) throws SdkClientException {
        String filePath = dirName + "/" + file.getName();

        PutObjectRequest request = new PutObjectRequest(bucketName, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        try {
            amazonS3.putObject(request);
        } catch (SdkClientException e) {
            throw new SdkClientException("Failed to upload file");
        } finally {
            file.delete();
        }
        return amazonS3.getUrl(bucketName, filePath).toString();
    }

    @Override
    public Optional<File> convert(MultipartFile multipartFile) throws IOException {
        // Generate File name
        File retFile = new File(UUID.randomUUID() + "-" + multipartFile.getOriginalFilename());

        if(retFile.createNewFile()) {
            try(FileOutputStream fos = new FileOutputStream(retFile)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(retFile);
        }
        return Optional.empty();
    }

    @Override
    public int delete(String fileURL) {
        try {
            deleteFile(fileURL);
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
        return 0;
    }

    @Override
    public int delete(List<String> fileURLs) {
        int len = fileURLs.size();
        int ret = 0;
        for(int i=0; i<len; i++) {
            try {
                deleteFile(fileURLs.get(i));
            } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException e) {
                ret = -1;
            }
        }

        return ret;
    }

    @Override
    public void deleteFile(String fileURL) throws UnsupportedEncodingException, ArrayIndexOutOfBoundsException {
        String[] urlParts = fileURL.split("/", 4);
        String fileName = urlParts[3];

        logger.info("Delete file : {}", fileName);
        String objKey = URLDecoder.decode(fileName, "UTF-8").trim();
        logger.info("Delete object : {}", objKey);
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, objKey);
        amazonS3.deleteObject(request);
    }
}
