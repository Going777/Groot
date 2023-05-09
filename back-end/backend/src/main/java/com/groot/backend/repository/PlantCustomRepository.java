package com.groot.backend.repository;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.entity.PlantEntity;

import java.util.List;

public interface PlantCustomRepository {

    List<PlantEntity> search(PlantSearchDTO plantSearchDTO);
}
