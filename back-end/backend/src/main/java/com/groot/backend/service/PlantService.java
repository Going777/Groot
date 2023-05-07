package com.groot.backend.service;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.dto.response.PlantIdentificationDTO;
import com.groot.backend.dto.response.PlantThumbnailDTO;
import com.groot.backend.entity.PlantEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public PlantIdentificationDTO identifyPlant(MultipartFile multipartFile) throws Exception;
}
