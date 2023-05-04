package com.groot.backend.service;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.dto.response.PlantDetailDTO;
import com.groot.backend.dto.response.PlantIdentificationDTO;
import com.groot.backend.dto.response.PlantThumbnailDTO;
import com.groot.backend.entity.PlantEntity;
import com.groot.backend.repository.PlantRepository;
import com.groot.backend.util.JsonParserUtil;
import com.groot.backend.util.PlantCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantServiceImpl implements PlantService{

    private final Logger logger = LoggerFactory.getLogger(PlantServiceImpl.class);

    private final PlantRepository plantRepository;

    @Value("${plant.temp.dir}")
    private String plantTempDir;
    @Value("${plantnet.apiKey}")
    private String plantNetApiKey;

    private String plantNetUrl = "https://my-api.plantnet.org/v2/identify/all?include-related-images=false&no-reject=false&lang=en&api-key=";

    @Override
    public List<String> getNameList() {
        logger.info("Get Name list");
        List<PlantEntity> plantEntityList = plantRepository.findAll();
//        List<PlantNameDTO> ret = new ArrayList<>(plantEntityList.size());
        List<String> ret = new ArrayList<>(plantEntityList.size());

        plantEntityList.forEach((plantEntity) -> {
//            ret.add(PlantNameDTO.builder().plantName(plantEntity.getKrName()).build());
            ret.add(plantEntity.getKrName());
        });

        return ret;
    }

    @Override
    public PlantDetailDTO plantDetail(Long plantId) {
        logger.info("Find plant by plantId : {}", plantId);
        PlantEntity plantEntity;

        try {
            plantEntity = plantRepository.findById(plantId).get();
        } catch (NoSuchElementException e) {
            logger.info("No plant found : {}", plantId);
            return null;
        }

        PlantDetailDTO plantDetailDTO = PlantDetailDTO.builder()
                .plantId(plantEntity.getId())
                .krName(plantEntity.getKrName())
                .sciName(plantEntity.getSciName())
                .description(plantEntity.getDescription())
                .mgmtLevel(PlantCodeUtil.mgmtLevelCode[plantEntity.getMgmtLevel()])
                .mgmtDemand(plantEntity.getMgmtDemand())
                .place(plantEntity.getPlace())
//                .smellDegree(PlantCodeUtil.smellCode[plantEntity.getSmellDegree()])
                .grwType(plantEntity.getGrwType())
                .insectInfo(plantEntity.getInsectInfo())
                .mgmtTip(plantEntity.getMgmtTip())
                .minGrwTemp(plantEntity.getMinGrwTemp()).maxGrwTemp(plantEntity.getMaxGrwTemp())
                .minHumidity(plantEntity.getMinHumidity()).maxHumidity(plantEntity.getMaxHumidity())
                .waterCycle(PlantCodeUtil.waterCycleCode[plantEntity.getWaterCycle()%53000])
                .img(plantEntity.getImg())
                .build();


        return plantDetailDTO;
    }

    @Override
    public List<PlantThumbnailDTO> plantList(PlantSearchDTO plantSearchDTO) {
        logger.info("search plant list");
        List<PlantThumbnailDTO> ret = new ArrayList<>(12);
        Pageable pageable = PageRequest.of(plantSearchDTO.getPage(), 12);

        List<PlantEntity> list = plantRepository.findByKrNameContains(plantSearchDTO.getName(), pageable);

        list.forEach(plantEntity -> {
            ret.add(PlantThumbnailDTO.builder()
                            .plantId(plantEntity.getId())
                            .krName(plantEntity.getKrName())
                            .img(plantEntity.getImg())
                            .build());
        });
        return ret;
    }

    @Override
    public PlantIdentificationDTO identifyPlant(MultipartFile multipartFile) throws Exception {
        logger.info("Identify by : {}", multipartFile.getOriginalFilename());

        File file = convertFile(multipartFile);

        ResponseEntity<String> response = getResponse(file);
        file.delete();

        logger.info("response : {}", response.getStatusCode());
        if(response.getStatusCode() == HttpStatus.OK) {
            logger.info("Failed to get response. URL : {}, API_KEY : {}", plantNetUrl, plantNetApiKey);
            String[][] result = JsonParserUtil.plantNameParser(response.getBody());
            return searchFromDB(result);
        }
        else if (response.getStatusCode() == HttpStatus.UNSUPPORTED_MEDIA_TYPE) {
            logger.info("Wrong file format");
            throw new InvalidContentTypeException();
        }
        else {
            throw new Exception();
        }
    }

    /**
     * Convert multipart File to file
     * @param multipartFile
     * @return File
     * @throws IOException : failed to create or write file
     */
    private File convertFile(MultipartFile multipartFile) throws IOException{
        File retFile = new File(plantTempDir + multipartFile.getOriginalFilename());

        try {
            retFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(retFile);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (IOException e) {
            logger.info("Failed to create file");
            logger.info("Delete file : {}", retFile.delete());
            throw new IOException();
        }

        return retFile;
    }

    /**
     * Send request by rest template
     * @param file
     * @return Response entity with JSON body
     */
    private ResponseEntity<String> getResponse(File file) {
        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // request body
        MultiValueMap<String, Object> reqBody = new LinkedMultiValueMap<>();
        reqBody.add("images", new FileSystemResource(plantTempDir + file.getName()));

        // send request
        HttpEntity<MultiValueMap<String, Object>> reqEntity = new HttpEntity<>(reqBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = plantNetUrl + plantNetApiKey;

        return restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);
    }

    /**
     * search plant from db by scientific name
     * @param result top-k list with score
     * @return plant info
     */
    private PlantIdentificationDTO searchFromDB(String[][] result) {
        logger.info("search {} from database", result[0][0]);
        PlantEntity plantEntity = plantRepository.findBySciNameStartsWith(result[0][0]);

        if (plantEntity != null) {
            return PlantIdentificationDTO.builder()
                    .plantId(plantEntity.getId())
                    .krName(plantEntity.getKrName())
                    .sciName(plantEntity.getSciName())
                    .score((int)(Float.parseFloat(result[0][1]) * 100))
                    .build();

        }
        return null;
    }
}
