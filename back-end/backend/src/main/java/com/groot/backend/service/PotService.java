package com.groot.backend.service;

import com.groot.backend.controller.exception.WrongArticleException;
import com.groot.backend.dto.request.PotModifyDTO;
import com.groot.backend.dto.request.PotRegisterDTO;
import com.groot.backend.dto.request.PotTransferDTO;
import com.groot.backend.dto.response.PotDetailDTO;
import com.groot.backend.dto.response.PotListDTO;
import com.groot.backend.dto.response.PotTransferInfoDTO;
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
    public List<PotListDTO> potList(Long userPK, Boolean isArchive);

    /**
     * get detail of pot and plant
     * @param userPK
     * @param potId
     * @return pot and plant information
     * @throws NoSuchElementException when pot or plant not found
     * @throws AccessDeniedException for unauthorized access
     */
    public PotDetailDTO potDetail(Long userPK, Long potId);

    /**
     * modify pot image, name or environment
     * @param userPK
     * @param potId
     * @param potModifyDTO name, temp, illum, hum
     * @param multipartFile new image
     * @throws NoSuchElementException when pot not found
     * @throws AccessDeniedException for unauthorized access
     * @throws IllegalArgumentException for GONE
     * @throws Exception
     */
    public String modifyPot(Long userPK, Long potId, PotModifyDTO potModifyDTO, MultipartFile multipartFile) throws Exception;

    /**
     * Delete pot
     * @param userPK
     * @param potId
     * @return 1 for success
     * @throws NoSuchElementException when pot not found
     * @throws AccessDeniedException for unauthorized access
     */
    public int deletePot(Long userPK, Long potId) throws Exception;

    /**
     * toggle survival status
     * @param userPK
     * @param potId
     * @return next status (survival or not) : resurrection is not implemented yet
     * @throws NoSuchElementException when pot not found
     * @throws AccessDeniedException for unauthorized access
     */
    public boolean toggleStatus(Long userPK, Long potId) throws Exception;

    /**
     * Create pot transfer request. Pot Expires at previous user, not copied until accepted
     * @param fromUserPK user PK
     * @param potTransferDTO
     * @return PK of pot transfer
     * @throws NoSuchElementException one or more missing : pot, to user, article
     * @throws WrongArticleException article is not for share, or already transferred
     * @throws IllegalStateException pot status is not adequate, might be gone or shared already
     * @throws AccessDeniedException the pot does not belong to requested user
     */
    public Long createTransfer(Long fromUserPK, PotTransferDTO potTransferDTO) throws Exception;

    /**
     * Return list of received transfer requests
     * @param userPK
     * @return list of recieved transfer
     * @throws NoSuchElementException not found
     * @throws Exception
     */
    public List<PotTransferInfoDTO> getTransferList(Long userPK) throws Exception;

    /**
     * Accept pot transfer
     * @param userPK
     * @param transferId
     * @return Pot Id
     * @throws NoSuchElementException transfer Not found
     * @throws AccessDeniedException unauthorized access
     */
    public Long acceptTransfer(Long userPK, Long transferId) throws Exception;
}
