package com.groot.backend.service;

import com.groot.backend.dto.request.PotModifyDTO;
import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.response.PotDetailDTO;
import com.groot.backend.dto.response.PotListDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

public interface PotService {

    /**
     * Create pot
     * @param potRegisterDTO
     * @return pot id
     * @throws IOException for FILE UP ERROR
     * @throws NoSuchElementException for retrieve failure
     * @throws Exception for DB insert failure
     */
    public Long createPot(Long userPK, PotRegisterDTO potRegisterDTO, MultipartFile multipartFile)
        throws Exception;

    /**
     * get list of pot by user pk
     * @param userPK
     * @return list of pot
     * @throws java.util.NoSuchElementException when nothing found for userPK
     */
    public List<PotListDTO> potList(Long userPK);

    /**
     *
     * @param userPK
     * @param potId
     * @return
     * @throws NoSuchElementException when pot or plant not found
     * @throws AccessDeniedException for unauthorized access
     */
    public PotDetailDTO potDetail(Long userPK, Long potId);

    /**
     *
     * @param userPK
     * @param potId
     * @param potModifyDTO name, temp, illum, hum
     * @param multipartFile new image
     * @throws NoSuchElementException when pot not found
     * @throws AccessDeniedException for unauthorized access
     * @throws Exception
     */
    public String modifyPot(Long userPK, Long potId, PotModifyDTO potModifyDTO, MultipartFile multipartFile) throws Exception;
}
