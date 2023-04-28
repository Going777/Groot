package com.groot.backend.service;

import com.amazonaws.SdkClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public interface S3Service {
    /**
     * Upload single Multipart file. Converts to file and upload at S3
     * @param multipartFile
     * @param dirName : category name
     * @return file path
     * @throws IOException : failed to create or upload file
     */
    public String upload(MultipartFile multipartFile, String dirName) throws IOException;

    /**
     * Upload multiple Multipart files. Converts each file and upload at S3. Manages transactional
     * @param multipartFiles
     * @param dirName : category name
     * @return array of file path
     * @throws IOException : failed to create or upload file
     */
    public String[] upload(MultipartFile [] multipartFiles, String dirName) throws IOException;

    /**
     * Upload single file to S3
     * @param file
     * @param dirName : category name
     * @return file path
     * @throws SdkClientException : S3 error
     */
    public String uploadFile(File file, String dirName);

    /**
     * Convert multipart file to file
     * @param multipartFile
     * @return File
     * @throws IOException : failed to create file
     */
    public Optional<File> convert(MultipartFile multipartFile) throws IOException;

    /**
     * Delete single file
     * @param fileURL file url including bucket
     * @return 0 : success. -1 : failed - wrong input
     */
    public int delete(String fileURL);

    /**
     * Delete multiple files
     * @param fileURLs List of file urls. including bucket
     * @return 0 : success. -1 : failed - wrong input
     */
    public int delete(List<String> fileURLs);

    /**
     * Delete file of S3 server
     * @param fileURL File url
     * @throws UnsupportedEncodingException wrong input
     * @throws ArrayIndexOutOfBoundsException wrong input
     */
    public void deleteFile(String fileURL) throws UnsupportedEncodingException, ArrayIndexOutOfBoundsException;
}
