package com.groot.backend.service;

import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.response.CharacterDTO;
import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.dto.response.PotDetailDTO;
import com.groot.backend.dto.response.PotListDTO;
import com.groot.backend.entity.CharacterEntity;
import com.groot.backend.entity.PlantEntity;
import com.groot.backend.entity.PotEntity;
import com.groot.backend.repository.CharacterRepository;
import com.groot.backend.repository.PlantRepository;
import com.groot.backend.repository.PotRepository;
import com.groot.backend.repository.UserRepository;
import com.groot.backend.util.PlantCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PotServiceImpl implements PotService{

    private final PotRepository potRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final S3Service s3Service;
    private final Logger logger = LoggerFactory.getLogger(PotServiceImpl.class);
    @Override
    public Long createPot(Long userPK, PotRegisterDTO potRegisterDTO, MultipartFile multipartFile)
            throws IOException, NoSuchElementException, Exception {
        logger.info("Create pot : {}", potRegisterDTO.getPotName());
        String imgPath = "";


        try {
            imgPath = s3Service.upload(multipartFile, "pot");
            logger.info("image uploaded : {}", imgPath);

            PlantEntity plantEntity = plantRepository.findById(potRegisterDTO.getPlantId()).get();
            logger.info("Plant found : {}", plantEntity.getKrName());

            PotEntity potEntity = potRepository.save(
                    PotEntity.builder()
                            .userEntity(userRepository.findById(userPK).get())
                            .plantEntity(plantEntity)
                            .characterEntity(characterRepository.findByTypeAndLevel(PlantCodeUtil.characterCode(plantEntity.getGrwType()),0))
                            .name(potRegisterDTO.getPotName())
                            .imgPath(imgPath)
                            // default values might be modified
                            .temperature(potRegisterDTO.getTemperature() == 0 ? 20 : potRegisterDTO.getTemperature())
                            .illuminance(potRegisterDTO.getIlluminance() == 0 ? 500 : potRegisterDTO.getIlluminance())
                            .humidity(potRegisterDTO.getHumidity() == 0 ? 50 : potRegisterDTO.getHumidity())
                            .plantKrName(plantEntity.getKrName())
                            .build()
            );
            potRepository.save(potEntity);

            return potEntity.getId();
        } catch (IOException e) {
            logger.info("Failed to upload image");
            throw new IOException();

        } catch (IllegalArgumentException | NoSuchElementException e) {
            logger.info("Plant or User not found : plant {}, user {}", potRegisterDTO.getPlantId(), userPK);
            s3Service.delete(imgPath);
            throw new NoSuchElementException();

        } catch (Exception e) {
            logger.info("Failed to save pot entity with pot name: {}", potRegisterDTO.getPotName());
            logger.info("Related Exception : {}", e.getMessage());
            s3Service.delete(imgPath);
            throw new Exception();
        }
    }

    @Override
    public List<PotListDTO> potList(Long userPK) throws NoSuchElementException {
        logger.info("user pk : {}", userPK);

        List<PotEntity> list = potRepository.findAllByUserId(userPK);

        if(list == null || list.size() < 1) throw new NoSuchElementException();

        List<PotListDTO> ret = new ArrayList<>(list.size());

        list.forEach(potEntity -> {
            ret.add(PotListDTO.builder()
                            .potId(potEntity.getId())
                            .plantId(potEntity.getPlantId())
                            .potName(potEntity.getName())
                            .imgPath(potEntity.getImgPath())
                            .plantKrName(potEntity.getPlantKrName())
                            .dates(calcPeriod(potEntity.getCreatedDate()))
                            .createdTime(potEntity.getCreatedDate())
                            .waterDate(potEntity.getWaterDate())    // calc
                            .nutrientsDate(potEntity.getNutrientsDate())    // calc
                            .pruningDate(potEntity.getPruningDate())    // calc
                            .survival(potEntity.getSurvival())
                            .level(expToLevel(potEntity.getExperience()))   // level?
                            .characterId(potEntity.getCharacterId())    // id or path
                            .characterGLBPath(characterRepository.findById(potEntity.getCharacterId()).get().getGlbPath())
                            .characterPNGPath(characterRepository.findById(potEntity.getCharacterId()).get().getPngPath())
                            .build());
        });

        return ret;
    }

    @Override
    public PotDetailDTO potDetail(Long userPK, Long potId) throws NoSuchElementException{
        logger.info("Find pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get();
        if (potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        PotListDTO potListDTO = PotListDTO.builder()
                .potId(potEntity.getId())
                .plantId(potEntity.getPlantId())
                .potName(potEntity.getName())
                .imgPath(potEntity.getImgPath())
                .plantKrName(potEntity.getPlantKrName())
                .dates(calcPeriod(potEntity.getCreatedDate()))
                .createdTime(potEntity.getCreatedDate())
                .waterDate(potEntity.getWaterDate())    // calc
                .nutrientsDate(potEntity.getNutrientsDate())    // calc
                .pruningDate(potEntity.getPruningDate())    // calc
                .survival(potEntity.getSurvival())
                .level(expToLevel(potEntity.getExperience()))   // level?
                .characterId(potEntity.getCharacterId())    // id or path
                .build();

        PlantEntity plantEntity = potEntity.getPlantEntity();

        PlantDetailDTO plantDetailDTO = PlantDetailDTO.builder()
                .plantId(plantEntity.getId())
                .krName(plantEntity.getKrName())
                .sciName(plantEntity.getSciName())
                .description(plantEntity.getDescription())
                .mgmtLevel(PlantCodeUtil.mgmtLevelCode[plantEntity.getMgmtLevel()])
                .mgmtDemand(plantEntity.getMgmtDemand())
                .place(plantEntity.getPlace())
                .grwType(plantEntity.getGrwType())
                .insectInfo(plantEntity.getInsectInfo())
                .mgmtTip(plantEntity.getMgmtTip())
                .minGrwTemp(plantEntity.getMinGrwTemp()).maxGrwTemp(plantEntity.getMaxGrwTemp())
                .minHumidity(plantEntity.getMinHumidity()).maxHumidity(plantEntity.getMaxHumidity())
                .waterCycle(PlantCodeUtil.waterCycleCode[plantEntity.getWaterCycle()%53000])
                .img(plantEntity.getImg())
                .build();

        CharacterEntity characterEntity = characterRepository.findById(potEntity.getCharacterId()).get();

        CharacterDTO characterDTO = CharacterDTO.builder()
                .characterId(characterEntity.getId())
                .level(characterEntity.getLevel())
                .glbPath(characterEntity.getGlbPath())
                .pngPath(characterEntity.getPngPath())
                .build();

        return PotDetailDTO.builder().pot(potListDTO).plant(plantDetailDTO).character(characterDTO).build();
    }

    private int calcPeriod(LocalDateTime from) {
        LocalDateTime now = LocalDateTime.now();

        Period period = Period.between(from.toLocalDate(), now.toLocalDate());
        return period.getDays() + 1;
    }

    private int expToLevel(int exp) {
        return exp / 10;
    }
}
