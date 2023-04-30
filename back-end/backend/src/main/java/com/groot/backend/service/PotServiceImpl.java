package com.groot.backend.service;

import com.groot.backend.dto.request.PotDTO;
import com.groot.backend.entity.PotEntity;
import com.groot.backend.repository.PotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PotServiceImpl implements PotService{

    private final PotRepository potRepository;
    private final S3Service s3Service;
    private final Logger logger = LoggerFactory.getLogger(PotServiceImpl.class);
    @Override
    public Long createPot(PotDTO potDTO, MultipartFile multipartFile) {
        logger.info("Create pot : {}", potDTO.getPotName());
        String imgPath = "";

        // save image first
        try {
            imgPath = s3Service.upload(multipartFile, "pot");
        } catch (IOException e) {
            logger.info("Failed to upload image");
            return -1L;
        }

        PotEntity potEntity = potRepository.save(
                PotEntity.builder()
                        .userId(potDTO.getUserId())
                        .plantId(potDTO.getPlantId())
                        .characterId(potDTO.getCharacterId())
                        .name(potDTO.getPotName())
                        .imgPath(imgPath)
                        .temperature(potDTO.getTemperature())
                        .illuminance(potDTO.getIlluminance())
                        .humidity(potDTO.getHumidity())
                        .build()
        );

        potRepository.save(potEntity);

        return potEntity.getId();
    }
}
