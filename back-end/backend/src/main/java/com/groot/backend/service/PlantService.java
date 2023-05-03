package com.groot.backend.service;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.dto.response.PlantThumbnailDTO;
import com.groot.backend.entity.PlantEntity;

import java.util.List;

public interface PlantService {

    public List<String> getNameList();

    public PlantDetailDTO plantDetail(Long plantId);

    public List<PlantThumbnailDTO> plantList(PlantSearchDTO plantSearchDTO);
}
