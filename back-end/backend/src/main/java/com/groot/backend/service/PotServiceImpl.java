package com.groot.backend.service;

import com.groot.backend.dto.request.PotModifyDTO;
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
            String[] urls = getAssets(potEntity.getPlantEntity().getGrwType(), potEntity.getExperience(), potEntity.getSurvival());
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
                            .characterPNGPath(urls[0])
                            .characterGLBPath(urls[1])
                            .build());
        });

        return ret;
    }

    @Override
    public PotDetailDTO potDetail(Long userPK, Long potId) throws NoSuchElementException{
        logger.info("Find pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get();
        if (potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        String[] urls = getAssets(potEntity.getPlantEntity().getGrwType(), potEntity.getExperience(), potEntity.getSurvival());

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
                .characterPNGPath(urls[0])
                .characterGLBPath(urls[1])
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

        return PotDetailDTO.builder().pot(potListDTO).plant(plantDetailDTO).build();
    }

    @Override
    public String modifyPot(Long userPK, Long potId, PotModifyDTO potModifyDTO, MultipartFile multipartFile) throws Exception{
        logger.info("Modify pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get();

        if(potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        String srcImgPath = potEntity.getImgPath();
        String newImgPath = "";

        try {
            newImgPath = s3Service.upload(multipartFile, "pot");
            logger.info("Successfully uploaded : {}", newImgPath);
            if(potModifyDTO != null) potEntity.modify(newImgPath, potModifyDTO.getPotName(),
                        potModifyDTO.getTemperature(), potModifyDTO.getIlluminance(), potModifyDTO.getHumidity());
            else potEntity.modify(newImgPath, null, 0, 0, 0);

            potRepository.save(potEntity);

            s3Service.delete(srcImgPath);
            return newImgPath;
        } catch (IOException e) {
            logger.info("Failed to create file");
            throw new IOException();
        } catch (Exception e) {
            logger.info("failed to modify pot : {}, {}, {}", e.getCause(), e.getClass(), e.getStackTrace());
            if(newImgPath != "") s3Service.delete(newImgPath);
            throw new Exception();
        }
    }

    @Override
    public int deletePot(Long userPK, Long potId) throws Exception{
        logger.info("delete pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get(); // NoSuchElementException

        logger.info("pot {} found, delete it", potId);

        if(potEntity.getUserId() != userPK) throw new IllegalAccessException("Unauthorized");

        potRepository.delete(potEntity); // IllegalArgumentException

        return 0;
    }

    @Override
    public boolean toggleStatus(Long userPK, Long potId) throws Exception {
        logger.info("toggle status : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get(); // NoSuchElementException

        if(potEntity.getUserId() != userPK) throw new IllegalAccessException("Unauthorized");

        logger.info("pot {} found with status : {}", potId, potEntity.getSurvival());

        boolean ret = potEntity.toggleSurvival();
        potRepository.save(potEntity);

        return ret;
    }

    private int calcPeriod(LocalDateTime from) {
        LocalDateTime now = LocalDateTime.now();

        Period period = Period.between(from.toLocalDate(), now.toLocalDate());
        return period.getDays() + 1;
    }

    private int expToLevel(int exp) {
        int ret = exp/10;
        return (ret > 2) ? 2 : ret;
    }

    /**
     * returns character png and glb url
     * @param grwType
     * @param exp
     * @param survival
     * @return [png url, glb url]
     */
    private String[] getAssets(String grwType, int exp, boolean survival) {
        CharacterEntity characterEntity;
        if(!survival) {
            characterEntity =
                characterRepository.findByType(PlantCodeUtil.characterCode("gone"));
        }
        else {
            characterEntity = characterRepository.findByTypeAndLevel(PlantCodeUtil.characterCode(grwType), expToLevel(exp));
        }
        return new String[] {characterEntity.getPngPath(), characterEntity.getGlbPath()};
    }
}
