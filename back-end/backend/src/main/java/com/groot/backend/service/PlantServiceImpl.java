package com.groot.backend.service;

import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.dto.response.PlantNameDTO;
import com.groot.backend.entity.PlantEntity;
import com.groot.backend.repository.PlantRepository;
import com.groot.backend.util.PlantCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    @Override
    public PlantDetailDTO plantDetail(Long plantId) {
        logger.info("Find plant by plantId : {}", plantId);
        PlantEntity plantEntity;

        try {
            plantEntity = plantRepository.findById(plantId).get();
        } catch (NoSuchElementException e) {
            logger.info("No plant found : {}", plantId);
            return null;
        }

        PlantDetailDTO plantDetailDTO = PlantDetailDTO.builder()
                .plantId(plantEntity.getId())
                .krName(plantEntity.getKrName())
                .sciName(plantEntity.getSciName())
                .description(plantEntity.getDescription())
                .mgmtLevel(PlantCodeUtil.mgmtLevelCode[plantEntity.getMgmtLevel()])
                .mgmtDemand(plantEntity.getMgmtDemand())
                .place(plantEntity.getPlace())
                .smellDegree(PlantCodeUtil.smellCode[plantEntity.getSmellDegree()])
                .insectInfo(plantEntity.getInsectInfo())
                .mgmtTip(plantEntity.getMgmtTip())
                .minGrwTemp(plantEntity.getMinGrwTemp()).maxGrwTemp(plantEntity.getMaxGrwTemp())
                .minHumidity(plantEntity.getMinHumidity()).maxHumidity(plantEntity.getMaxHumidity())
                .waterCycle(PlantCodeUtil.waterCycleCode[plantEntity.getWaterCycle()%53000])
                .build();


        return plantDetailDTO;
    }
}
