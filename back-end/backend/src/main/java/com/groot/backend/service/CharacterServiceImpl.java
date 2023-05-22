package com.groot.backend.service;

import com.groot.backend.dto.response.CharacterCollectionDTO;
import com.groot.backend.dto.response.CharacterDTO;
import com.groot.backend.dto.response.CharacterImageDTO;
import com.groot.backend.entity.CharacterEntity;
import com.groot.backend.entity.PotEntity;
import com.groot.backend.repository.CharacterRepository;
import com.groot.backend.repository.PotRepository;
import com.groot.backend.util.PlantCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterServiceImpl implements CharacterService {

    private final Logger logger = LoggerFactory.getLogger(CharacterServiceImpl.class);
    private final CharacterRepository characterRepository;

    private final PotRepository potRepository;

    @Override
    public List<CharacterImageDTO> getImageList() {
        List<CharacterEntity> characterEntities = characterRepository.findAllByOrderByTypeAscLevelAsc();

        if(characterEntities == null || characterEntities.size() < 1) {
            logger.info("No characters found, check Database");
            throw new NoSuchElementException();
        }

        List<CharacterImageDTO> ret = new ArrayList<>(characterEntities.size());

        characterEntities.forEach(characterEntity -> {
            if(characterEntity.getType() > 11) return;
            ret.add(CharacterImageDTO.builder()
                            .grwType(PlantCodeUtil.characterName[characterEntity.getType().intValue()])
                            .level(characterEntity.getLevel())
                            .pngPath(characterEntity.getPngPath())
                            .build());
        });

        return ret;
    }

    @Override
    public List<Integer> getCollections(Long userPK) {
        List<PotEntity> potEntities = potRepository.findAllByUserIdOrderByCreatedDateDesc(userPK);
        logger.info("{} pots found for user : {}", potEntities.size(), userPK);

        if(potEntities == null || potEntities.size() < 1)
            throw new NoSuchElementException();

        List<Integer> ret = new ArrayList<>();
        boolean[] collected = new boolean[33];

        potEntities.forEach(potEntity -> { addCollection(potEntity, collected); });


        for(int i=0; i<collected.length; i++)
            if(collected[i])
                ret.add(i);

        return ret;
    }

    @Override
    public List<CharacterCollectionDTO> getAll(Long userPK) {
        List<CharacterImageDTO> imageList = getImageList();
        List<Integer> collectionList = getCollections(userPK);
        List<CharacterCollectionDTO> ret = new ArrayList<>(imageList.size());

        imageList.forEach(characterImageDTO -> {
            ret.add(CharacterCollectionDTO.builder()
                            .grwType(characterImageDTO.getGrwType())
                            .level(characterImageDTO.getLevel())
                            .pngPath(characterImageDTO.getPngPath())
                            .collected(false)
                            .build());
        });

        collectionList.forEach(integer -> {
            CharacterCollectionDTO characterCollectionDTO = ret.get(integer);
            characterCollectionDTO.updateCollected(true);
            logger.info("modified : {}", integer);
            ret.set(integer, characterCollectionDTO);
        });

        return ret;
    }

    /**
     * Add collection index to list
     * @param potEntity
     * @param collected
     */
    private void addCollection(PotEntity potEntity, boolean[] collected) {
        String grwType = potEntity.getPlantEntity().getGrwType().split(",")[0];
        int charLevel = (potEntity.getLevel() / 5) > 2 ? 2 : potEntity.getLevel() / 5;

        for(int i=0; i<=charLevel; i ++) {
            logger.info("collected : {} lv.{} {}", grwType, charLevel, (PlantCodeUtil.characterCode.get(grwType) - 1) * 3 + charLevel);
            collected[(PlantCodeUtil.characterCode.get(grwType) - 1) * 3 + i] = true;
        }
    }
}
