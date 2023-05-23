package com.groot.backend.service;

import com.groot.backend.dto.response.CharacterCollectionDTO;
import com.groot.backend.dto.response.CharacterDTO;
import com.groot.backend.dto.response.CharacterImageDTO;

import java.util.List;

public interface CharacterService {

    /**
     * Get all the png path of characters
     * @return CharacterImageDTO
     */
    public List<CharacterImageDTO> getImageList();

    /**
     * Get list of collections
     * @param userPK user pk
     * @return list of collections
     */
    public List<Integer> getCollections(Long userPK);

    /**
     * return list of characters with status(collected or not)
     * hope this method is never used...
     * @param userPK
     * @return
     */
    public List<CharacterCollectionDTO> getAll(Long userPK);

    /**
     * return URL of glb file
     * @param position
     * @return
     */
    public String getGlb(int position);
}
