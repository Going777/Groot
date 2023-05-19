package com.groot.backend.service;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PlantService {

    public List<String> getNameList();

    public PlantDetailDTO plantDetail(Long plantId);

    public List<PlantThumbnailDTO> plantList(PlantSearchDTO plantSearchDTO);

    /**
     *
     * @param multipartFile
     * @return result of identification
     * @throws Exception : InvalidContentTypeException, IOException, Exception
     */
    public PlantWithCharacterDTO identifyPlant(MultipartFile multipartFile) throws Exception;

    public PlantEnvironmentDTO getAdequateEnv(Long plantId) throws Exception;

    /**
     * returns Plant information
     * @param plantId
     * @return PlantWithCharacterDTO
     * @throws java.util.NoSuchElementException for plant not found
     */
    public PlantWithCharacterDTO getIntroduction(Long plantId) throws Exception;
}
