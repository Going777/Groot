package com.groot.backend.service;

import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.response.PotListDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PotService {

    /**
     * Create pot
     * @param potRegisterDTO
     * @return pot id, -1 for wrong input, -2 for UL failure, -3 for server error
     */
    public Long createPot(Long userPK, PotRegisterDTO potRegisterDTO, MultipartFile multipartFile);

    /**
     * get list of pot by user pk
     * @param userPK
     * @return list of pot
     */
    public List<PotListDTO> potList(Long userPK);
}
