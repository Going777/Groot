package com.groot.backend.service;

import com.groot.backend.dto.response.PlantNameDTO;
import com.groot.backend.entity.PlantEntity;
import com.groot.backend.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantServiceImpl implements PlantService{

    private final Logger logger = LoggerFactory.getLogger(PlantServiceImpl.class);

    private final PlantRepository plantRepository;

    @Override
    public List<String> getNameList() {
        logger.info("Get Name list");
        List<PlantEntity> plantEntityList = plantRepository.findAll();
//        List<PlantNameDTO> ret = new ArrayList<>(plantEntityList.size());
        List<String> ret = new ArrayList<>(plantEntityList.size());

        plantEntityList.forEach((plantEntity) -> {
//            ret.add(PlantNameDTO.builder().plantName(plantEntity.getKrName()).build());
            ret.add(plantEntity.getKrName());
        });

        return ret;
    }
}
