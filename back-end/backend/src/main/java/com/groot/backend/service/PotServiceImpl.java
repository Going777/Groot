package com.groot.backend.service;

import com.groot.backend.dto.request.PotModifyDTO;
import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.response.*;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
import com.groot.backend.util.PlantCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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
    private final PlanRepository planRepository;
    private final CharacterRepository characterRepository;
    private final S3Service s3Service;
    private final Logger logger = LoggerFactory.getLogger(PotServiceImpl.class);
    @Override
    public Long createPot(Long userPK, PotRegisterDTO potRegisterDTO, MultipartFile multipartFile)
            throws IOException, NoSuchElementException, Exception {
        logger.info("Create pot : {}", potRegisterDTO.getPotName());
        String imgPath = "";


        try {
            if(multipartFile != null && !multipartFile.isEmpty()) {
                imgPath = s3Service.upload(multipartFile, "pot");
                logger.info("image uploaded : {}", imgPath);
            }

            PlantEntity plantEntity = plantRepository.findById(potRegisterDTO.getPlantId()).get();
            UserEntity userEntity = userRepository.findById(userPK).get();
            logger.info("Plant found : {}", plantEntity.getKrName());

            PotEntity potEntity = potRepository.save(
                    PotEntity.builder()
                            .userEntity(userEntity)
                            .plantEntity(plantEntity)
                            .name(potRegisterDTO.getPotName())
                            .imgPath(imgPath == "" ? plantEntity.getImg() : imgPath)
                            // default values might be modified
                            .temperature(potRegisterDTO.getTemperature() == 0 ? 20 : potRegisterDTO.getTemperature())
                            .illuminance(potRegisterDTO.getIlluminance() == 0 ? 500 : potRegisterDTO.getIlluminance())
                            .humidity(potRegisterDTO.getHumidity() == 0 ? 50 : potRegisterDTO.getHumidity())
                            .plantKrName(plantEntity.getKrName())
                            .build()
            );
            potRepository.save(potEntity);

            List<PlanEntity> plans = new ArrayList<>();
            plans.add(PlanEntity.builder()
                    .potEntity(potEntity)
                    .userEntity(userEntity)
                    .code(0)
                    .dateTime(LocalDateTime.now()
//                            .plusDays(PlantCodeUtil.waterCycle[plantEntity.getWaterCycle()%53000])
                            .withHour(9).withMinute(0).withSecond(0)
                    )
                    .done(false)
                    .build()
            );

            plans.add(PlanEntity.builder()
                    .potEntity(potEntity)
                    .userEntity(userEntity)
                    .code(1)
                    .dateTime(LocalDateTime.now()
                            .plusMonths(6)
                            .withHour(9).withMinute(0).withSecond(0)
                    )
                    .done(false)
                    .build()
            );

            plans.add(PlanEntity.builder()
                    .potEntity(potEntity)
                    .userEntity(userEntity)
                    .code(2)
                    .dateTime(LocalDateTime.now()
                            .plusMonths(12)
                            .withHour(9).withMinute(0).withSecond(0)
                    )
                    .done(false)
                    .build()
            );

            planRepository.saveAll(plans);

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
    public List<PotListDTO> potList(Long userPK, Boolean isArchive) throws NoSuchElementException {
        logger.info("user pk : {}", userPK);

        List<PotEntity> list;
        if(isArchive) {
            logger.info("get archive");
            list = potRepository.findAllByUserId(userPK);
        }
        else {
            logger.info("get active pots");
            list = potRepository.findAllByUserIdAndSurvival(userPK, true);
        }

        if(list == null || list.size() < 1) throw new NoSuchElementException();

        List<PotListDTO> ret = new ArrayList<>(list.size());

        list.forEach(potEntity -> {
            ret.add(buildListDTO(potEntity));
        });

        return ret;
    }

    @Override
    public PotDetailDTO potDetail(Long userPK, Long potId) throws NoSuchElementException{
        logger.info("Find pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get();
        if (potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        PotListDTO potListDTO = buildListDTO(potEntity);

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

        List<PlanEntity> planEntities = planRepository.findAllByPotId(potId);

        PlanWithDateDTO[] plans = new PlanWithDateDTO[3];

        planEntities.forEach(planEntity -> {
            plans[planEntity.getCode()] = PlanWithDateDTO.builder()
                                    .code(planEntity.getCode())
                                    .dateTime(planEntity.getDateTime())
                                    .build();
        });

        return PotDetailDTO.builder()
                .pot(potListDTO)
                .plant(plantDetailDTO)
                .waterDate(plans[0])
                .nutrientsDate(plans[1])
                .pruningDate(plans[2])
                .build();
    }

    @Override
    public String modifyPot(Long userPK, Long potId, PotModifyDTO potModifyDTO, MultipartFile multipartFile) throws Exception{
        logger.info("Modify pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get();

        if(potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        String srcImgPath = potEntity.getImgPath();
        String newImgPath = "";

        try {
            if(multipartFile != null) {
                newImgPath = s3Service.upload(multipartFile, "pot");
                logger.info("Successfully uploaded : {}", newImgPath);
            }
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
    @Transactional
    public int deletePot(Long userPK, Long potId) throws Exception{
        logger.info("delete pot : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get(); // NoSuchElementException

        logger.info("pot {} found, delete it", potId);

        if(potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        potRepository.delete(potEntity); // IllegalArgumentException
        planRepository.deleteAllByPotId(potId);
        
        return 0;
    }

    @Override
    @Transactional
    public boolean toggleStatus(Long userPK, Long potId) throws Exception {
        logger.info("toggle status : {}", potId);

        PotEntity potEntity = potRepository.findById(potId).get(); // NoSuchElementException

        if(potEntity.getUserId() != userPK) throw new AccessDeniedException("Unauthorized");

        logger.info("pot {} found with status : {}", potId, potEntity.getSurvival());

        if(potEntity.getSurvival()) {
            potEntity.toggleSurvival();
            potRepository.save(potEntity);

            planRepository.deleteAllByPotId(potId);

            return false;
        }
        else {
            // not implemented yet
//            potEntity.toggleSurvival();
//            potRepository.save(potEntity);
//            return true;
            throw new NotYetImplementedException();
        }
    }

    /**
     * calculate days
     * @param from
     * @return count
     */
    private int calcPeriod(LocalDateTime from) {
        LocalDateTime now = LocalDateTime.now();

        Period period = Period.between(from.toLocalDate(), now.toLocalDate());
        return period.getDays() + 1;
    }

    /**
     * Convert experience to level
     * @param exp
     * @return level
     */
    private int expToLevel(int exp) {
        int ret = exp/10;
        return (ret > 2) ? 2 : ret;
    }

    private int levelToCharacter(int level) {
        int ret = level / 5;
        return ret > 2 ? 2 : ret;
    }

    /**
     * returns character png and glb url
     * @param grwType
     * @param exp
     * @param survival
     * @return [png url, glb url]
     */
    private String[] getAssets(String grwType, int exp, int level, boolean survival) {
        CharacterEntity characterEntity;
        if(!survival) {
            characterEntity =
                characterRepository.findByType(PlantCodeUtil.characterCode("gone"));
        }
        else {
//            characterEntity = characterRepository.findByTypeAndLevel(PlantCodeUtil.characterCode(grwType), expToLevel(exp));
            characterEntity = characterRepository.findByTypeAndLevel(PlantCodeUtil.characterCode(grwType), levelToCharacter(level));
        }
        return new String[] {characterEntity.getPngPath(), characterEntity.getGlbPath()};
    }

    /**
     * Build PotListDTO by PotEntity
     * @param potEntity
     * @return PotListDTO
     */
    public PotListDTO buildListDTO(PotEntity potEntity) {
        String[] urls = getAssets(potEntity.getPlantEntity().getGrwType(), potEntity.getExperience(), potEntity.getLevel(), potEntity.getSurvival());
        return PotListDTO.builder()
                .potId(potEntity.getId())
                .plantId(potEntity.getPlantId())
                .potName(potEntity.getName())
                .imgPath(potEntity.getImgPath())
                .plantKrName(potEntity.getPlantKrName())
                .dates(calcPeriod(potEntity.getCreatedDate()))
                .createdTime(potEntity.getCreatedDate())
                .waterDate(potEntity.getWaterDate())
                .nutrientsDate(potEntity.getNutrientsDate())
                .pruningDate(potEntity.getPruningDate())
                .survival(potEntity.getSurvival())
                .experience(potEntity.getExperience())
//                .level(expToLevel(potEntity.getExperience()))   // level?
                .level(potEntity.getLevel())
                .characterPNGPath(urls[0])
                .characterGLBPath(urls[1])
                .build();
    }

    /**
     * Calculate next watering date - prototype
     * @param lastDate
     * @param potEntity
     * @return
     */
    public LocalDateTime calcNextWaterDate(LocalDateTime lastDate, PotEntity potEntity) {
        PlantEntity plantEntity = potEntity.getPlantEntity();
        int[] waterPeriods = PlantCodeUtil.waterPeriods[plantEntity.getWaterCycle() % 53000];

        int tempRange = inRange(potEntity.getTemperature(), plantEntity.getMinGrwTemp(), plantEntity.getMaxGrwTemp());
        int humidRange = inRange(potEntity.getHumidity(), plantEntity.getMinHumidity(), plantEntity.getMaxHumidity());

        int dateDiff = waterPeriods[1] - waterPeriods[0];
        int dateAvg = (waterPeriods[0] + waterPeriods[1]) / 2;

        int afterDate = (int)(dateAvg + 0.25*(-tempRange + humidRange) * dateDiff);
        return lastDate.plusDays(afterDate);
    }

    /**
     * Simply add month from date time
     * @param lastDate
     * @param month
     * @return
     */
    public LocalDateTime calcNextDate(LocalDateTime lastDate, int month) {
        return lastDate.plusMonths(month);
    }

    /**
     * returns where target value is at
     * @param target
     * @param min
     * @param max
     * @return -1(smaller) 0(in range) +1(larger)
     */
    public int inRange(double target, double min, double max) {
        return (min < target) ? ((target < max) ? 0 : 1) : -1;
    }
}
