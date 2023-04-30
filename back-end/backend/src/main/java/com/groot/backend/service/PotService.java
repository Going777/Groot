package com.groot.backend.service;

import com.groot.backend.dto.request.PotDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PotService {

    /**
     * Create pot
     * @param potDTO
     * @return pot id
     */
    public Long createPot(PotDTO potDTO, MultipartFile multipartFile);
}
