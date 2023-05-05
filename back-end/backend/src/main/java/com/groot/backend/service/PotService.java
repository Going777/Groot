package com.groot.backend.service;

import com.groot.backend.dto.request.PotRegisterDTO;
import org.springframework.web.multipart.MultipartFile;

public interface PotService {

    /**
     * Create pot
     * @param potRegisterDTO
     * @return pot id, -1 for wrong input, -2 for UL failure, -3 for server error
     */
    public Long createPot(Long userPK, PotRegisterDTO potRegisterDTO, MultipartFile multipartFile);
}
