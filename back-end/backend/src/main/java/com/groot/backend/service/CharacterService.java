package com.groot.backend.service;

import com.groot.backend.dto.response.CharacterImageDTO;

import java.util.List;

public interface CharacterService {

    /**
     * Get all the png path of characters
     * @return CharacterImageDTO
     */
    public List<CharacterImageDTO> getImageList();
}
