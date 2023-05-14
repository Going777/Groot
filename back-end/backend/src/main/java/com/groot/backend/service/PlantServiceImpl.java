package com.groot.backend.service;

import com.groot.backend.dto.request.PlantSearchDTO;
import com.groot.backend.dto.response.*;
import com.groot.backend.entity.CharacterEntity;
import com.groot.backend.entity.PlantEntity;
import com.groot.backend.repository.CharacterRepository;
import com.groot.backend.repository.PlantRepository;
import com.groot.backend.util.JsonParserUtil;
import com.groot.backend.util.PlantCodeUtil;
import com.groot.backend.util.RestTemplateErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantServiceImpl implements PlantService{

    private final Logger logger = LoggerFactory.getLogger(PlantServiceImpl.class);

    private final PlantRepository plantRepository;

    private final CharacterRepository characterRepository;

    @Value("${plant.temp.dir}")
    private String plantTempDir;
    @Value("${plantnet.apiKey}")
    private String plantNetApiKey;

//    private String plantNetUrl = "https://my-api.plantnet.org/v2/identify/all?include-related-images=false&no-reject=false&lang=en&api-key=";
    private String plantNetUrl = "https://my-api.plantnet.org/v2/identify/prosea?include-related-images=false&no-reject=false&lang=en&api-key=";

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
        List<PlantThumbnailDTO> ret = new ArrayList<>(30);
        Pageable pageable = PageRequest.of(plantSearchDTO.getPage(), 30);

        List<PlantEntity> list = plantRepository.search(plantSearchDTO);

        logger.info("{} plants found", list.size());

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
    public PlantWithCharacterDTO identifyPlant(MultipartFile multipartFile) throws Exception {
        logger.info("Identify by : {}", multipartFile.getOriginalFilename());

        File file = convertFile(multipartFile);

        ResponseEntity<String> response = getResponse(file);
        logger.info("response : {}", response.getStatusCode());
        file.delete();

        if(response.getStatusCode() == HttpStatus.OK) {
//            logger.info("Failed to get response. URL : {}, API_KEY : {}", plantNetUrl, plantNetApiKey);
            String[][] result = JsonParserUtil.plantNameParser(response.getBody());
            return searchFromDB(result);
        }
        else if (response.getStatusCode() == HttpStatus.UNSUPPORTED_MEDIA_TYPE
                || response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            logger.info("Wrong file format");
            throw new InvalidContentTypeException();
        }
        else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            logger.info("image file is not a plant");
            return PlantWithCharacterDTO.builder()
                    .plantIdentificationDTO(defaultReturn(0))
                    .characterAssetDTO(getAsset(plantRepository.findById(19449L).get()))
                    .build();
        }
        else {
            logger.info("Plantnet request failed with response code : {}", response.getStatusCode());
            throw new Exception();
        }
    }

    @Override
    public PlantEnvironmentDTO getAdequateEnv(Long plantId) throws Exception {
        logger.info("Find plant : {}", plantId);

        try {
            PlantEntity plantEntity = plantRepository.findById(plantId).get();
            int lightDemand = plantEntity.getLightDemand();

            return PlantEnvironmentDTO.builder()
                    .minLux(PlantCodeUtil.lightDemand[lightDemand][0])
                    .maxLux(PlantCodeUtil.lightDemand[lightDemand][1])
                    .build();

        } catch (NoSuchElementException e) {
            logger.info("Failed to find plant : {}", plantId);
            throw e;
        }
    }

    @Override
    public PlantWithCharacterDTO getIntroduction(Long plantId) throws Exception {
        logger.info("Find Plant : {}", plantId);
        PlantEntity plantEntity = plantRepository.findById(plantId).get();

        return PlantWithCharacterDTO.builder()
                    .plantIdentificationDTO(buildIdentificationDTO(plantEntity, "0"))
                    .characterAssetDTO(getAsset(plantEntity))
                    .build();
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
        RestTemplate restTemplate = new RestTemplateBuilder()
                    .errorHandler(new RestTemplateErrorHandler()).build();

        String url = plantNetUrl + plantNetApiKey;

//        return restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, reqEntity, String.class);
        logger.info("response : {}", response.getStatusCode());
        return response;
    }

    /**
     * search plant from db by scientific name
     * @param result top-k list with score
     * @return plant info and character asset, sanse for not found
     */
    private PlantWithCharacterDTO searchFromDB(String[][] result) {
        logger.info("search {} from database", result[0][0]);
        String regex = "";
        for(int i=0; i< result.length; i++) {
            regex += result[i][0].split(" ")[0];
            if(i != result.length - 1) regex += "|";
        };

        logger.info("Load all matched plants from DB : {}", regex);
        List<PlantEntity> plantEntities = plantRepository.findBySciNameRegex(regex);

        logger.info("plants found : {}", plantEntities.size());

        if(plantEntities.size() > 0) {
            logger.info("searching found : {}", plantEntities.size());
            // order might be changed - current : counts
            String[][] plantOrder = countPlantFreq(regex);

            for(int i=0; i<plantOrder.length; i++) {
                logger.info("Find escherichia : {} th : {}", i, plantOrder[i][0]);
                String[][] candidates = getCandidates(result, plantOrder[i][0]);
                String escherichia = plantOrder[i][0];
                if(candidates.length == 0 || plantEntities.stream().noneMatch(plantEntity -> {
                    return plantEntity.getSciName().startsWith(escherichia);
                })) continue;

                logger.info("{} Found", escherichia);

                PlantEntity plantEntity = null;

                for(int j=0; j<candidates.length; j++) {
                    for(int k=0; k<plantEntities.size(); k++) {
                        if(plantEntity == null && plantEntities.get(k).getSciName().startsWith(plantOrder[i][0])) {
                            plantEntity = plantEntities.get(k);
                        }
                        if(plantEntities.get(k).getSciName().startsWith(candidates[j][0])) {
                            plantEntity = plantEntities.get(k);
                            logger.info("{} found, return", plantEntity.getSciName());

                            return PlantWithCharacterDTO.builder()
                                    .plantIdentificationDTO(buildIdentificationDTO(plantEntity, result[0][1]))
                                    .characterAssetDTO(getAsset(plantEntity))
                                    .build();
                        }
                    }
                }
                logger.info("No exact matches found : {}", plantOrder[i][0]);
                return PlantWithCharacterDTO.builder()
                        .plantIdentificationDTO(buildIdentificationDTO(plantEntity, result[0][1]))
                        .characterAssetDTO(getAsset(plantEntity))
                        .build();
            }
            throw new NotYetImplementedException();
        }
        // return for not found
        else {
            logger.info("Failed to find plant from db");
            return PlantWithCharacterDTO.builder()
                    .plantIdentificationDTO(defaultReturn(11))
                    .characterAssetDTO(getAsset(plantRepository.findById(19449L).get()))
                    .build();
        }
//        return null;
    }

    /**
     * Count numbers of species[0] in regex
     * @param regex
     * @return array with name and cound
     */
    public String[][] countPlantFreq(String regex) {
        logger.info("Get frequency from : {}", regex);
        Map<String, Integer> map = new HashMap<>();
        List<Integer> counts = new ArrayList<>();
        String[] species = regex.split("\\|");
        int index = 0;

        for(int i=0; i<species.length; i++) {
            if (map.get(species[i]) == null) {
                map.put(species[i], index);
                counts.add(index++, 1);
            } else {
                int idx = map.get(species[i]);
                int cnt = counts.get(idx);
                counts.remove(idx);
                counts.add(idx, cnt + 1);
            }
        }

        String[][] ret = new String[index][2];

        map.forEach((key, value) -> {
            ret[value][0] = key;
            ret[value][1] = Integer.toString(counts.get(value));
            logger.info("count results : {}, {}", ret[value][0], ret[value][1]);
        });

        Arrays.sort(ret, (o1, o2) -> {
            return Integer.parseInt(o2[1]) - Integer.parseInt(o1[1]);
        });
        logger.info("Best match : {}, {}", ret[0][0], ret[0][1]);
        return ret;
    }

    /**
     * get candidates of species by escherichia
     * @param result
     * @param escherichia
     * @return species names and compensated scores
     */
    private String[][] getCandidates(String[][] result, String escherichia) {
        float total_score = 0;
        List<String[]> candidates = new ArrayList<>();

        for(int i=0; i<result.length; i++) {
            if(result[i][0].contains(escherichia)) {
                candidates.add(result[i]);
                total_score += Float.parseFloat(result[i][1]);
            }
        }

        String[][] ret = new String[candidates.size()][2];
        for(int i=0; i<candidates.size(); i++) {
            ret[i][0] = candidates.get(i)[0];
            ret[i][1] = Float.toString(Float.parseFloat(candidates.get(i)[1]) / total_score);
        }
        return ret;
    }

    /**
     * Build PlantIdentificationDTO by PlantEntity
     * @param plantEntity
     * @param score as String, will parse as integer automatically
     * @return PlantIdentificationDTO
     */
    private PlantIdentificationDTO buildIdentificationDTO(PlantEntity plantEntity, String score) {
        return PlantIdentificationDTO.builder()
                .plantId(plantEntity.getId())
                .krName(plantEntity.getKrName())
                .sciName(plantEntity.getSciName())
                .score((int)(Float.parseFloat(score) * 100))
                .grwType(plantEntity.getGrwType())
                .mgmtLevel(PlantCodeUtil.mgmtLevelCode[plantEntity.getMgmtLevel()])
                .build();
    }

    /**
     * find character assets resources
     * @return
     */
    private CharacterAssetDTO getAsset(PlantEntity plantEntity) {
        CharacterEntity characterEntity =
                characterRepository.findByTypeAndLevel(PlantCodeUtil.characterCode(plantEntity.getGrwType()), 0);

        return CharacterAssetDTO.builder()
                .pngPath(characterEntity.getPngPath())
                .glbPath(characterEntity.getGlbPath())
                .build();
    }

    private PlantIdentificationDTO defaultReturn(int score) {
        PlantEntity plantEntity = plantRepository.findById(19449L).get();
        return PlantIdentificationDTO.builder()
                .plantId(plantEntity.getId())
                .krName(plantEntity.getKrName())
                .sciName(plantEntity.getSciName())
                .score(score)
                .build();
    }
}
