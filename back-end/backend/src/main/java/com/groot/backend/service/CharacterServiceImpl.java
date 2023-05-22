package com.groot.backend.service;

import com.groot.backend.dto.response.CharacterImageDTO;
import com.groot.backend.entity.CharacterEntity;
import com.groot.backend.repository.CharacterRepository;
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
public class CharacterServiceImpl implements CharacterService {

    private final Logger logger = LoggerFactory.getLogger(CharacterServiceImpl.class);
    private final CharacterRepository characterRepository;

    @Override
    public List<CharacterImageDTO> getImageList() {
        List<CharacterEntity> characterEntities = characterRepository.findAll();

        if(characterEntities == null || characterEntities.size() < 1) {
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
}
