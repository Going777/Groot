package com.groot.backend.repository;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.entity.PlantEntity;
import com.sun.jdi.request.InvalidRequestStateException;

import java.util.List;

public interface PlantCustomRepository {

    /**
     * Find by conditions
     * @param plantSearchDTO
     * @return
     * @throws InvalidRequestStateException
     */
    List<PlantEntity> search(PlantSearchDTO plantSearchDTO) throws InvalidRequestStateException;
}
