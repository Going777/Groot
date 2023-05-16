package com.groot.backend.service;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.request.BookmarkDTO;
import com.groot.backend.dto.request.ShareStatusDTO;
import com.groot.backend.dto.response.*;
import com.groot.backend.entity.*;
import com.groot.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ArticleServiceImpl implements ArticleService{
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ArticleImageRepository articleImageRepository;
    private final ArticleBookmarkRepository aBookmarkRepo;
    private final RegionRepository regionRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;
    private final TagCountRepository tagCountRepository;

    private final String SHARE_KEY = "share";
    private final String FREE_KEY = "free";
    private final String QNA_KEY = "qna";
    private final String TIP_KEY = "tip";
    private final Map<String, String> keyMap = new HashMap<>(){{
        put("나눔", SHARE_KEY);
        put("자유", FREE_KEY);
        put("QnA", QNA_KEY);
        put("Tip", TIP_KEY);
    }};


    // 지역명 리스트 조회
    @Override
    public List<String> readRegion() {
        List<RegionEntity> regions = regionRepository.findAll();
        List<String> result = new ArrayList<>();
        for(RegionEntity entity : regions){
            result.add(entity.getRegion());
        }
        return result;
    }

    // 게시글 존재 여부 확인
    @Override
    public boolean existedArticleId(Long articleId) {
        return articleRepository.existsById(articleId);
    }

    // 인기 태그 조회
    @Override
    public List<TagRankDTO> readTagRanking(String category) {
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples;

//        String key = "ranking";
        String key = keyMap.get(category);

        if(ZSetOperations.size(key) >= 5){
            typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 4);  //score순으로 5개 보여줌
        }else {
            typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, ZSetOperations.size(key));
        }

        List<TagRankDTO> result = typedTuples.stream().map(TagRankDTO::convertToTagRankDTO).collect(Collectors.toList());

        // redis에 조회되는 값이 없으면 mysql에서 태그 데이터 가져오기
        if(result.size() == 0){
        // mysql-tag table 태그 이름 redis에 올리기
            // 태그 count 집계
            resetTagCount();
            // 전체 게시글에서 태그 집계 해오기
            List<TagCountEntity> tagcounts = tagCountRepository.findAll();

            // tag count 테이블에 데이터가 없으면 리턴
            if(tagcounts.size() == 0) return result;

            // redis에 추가
            for(TagCountEntity tagCountEntity : tagcounts){
                ZSetOperations.add(keyMap.get(tagCountEntity.getCategory()), tagCountEntity.getTag(), tagCountEntity.getCount());
            }
            ZSetOperations = redisTemplate.opsForZSet();

            if(ZSetOperations.size(key) >= 5){
                typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 4);  //score순으로 5개 보여줌
            }else {
                typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, ZSetOperations.size(key));
            }

            result = typedTuples.stream().map(TagRankDTO::convertToTagRankDTO).collect(Collectors.toList());
        }
        return result;
    }

    // tag count 집계
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul") // 오전 1시에 리셋
    @Override
    public void updateTagCountTable() {
        // tag count 집계
        resetTagCount();

        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();

        // redis 리셋
        ZSetOperations.getOperations().delete(SHARE_KEY);
        ZSetOperations.getOperations().delete(FREE_KEY);
        ZSetOperations.getOperations().delete(QNA_KEY);
        ZSetOperations.getOperations().delete(TIP_KEY);


        // tag_count 태그 이름 redis에 올리기
        List<TagCountEntity> list = tagCountRepository.findAll();
        for(TagCountEntity tagCountEntity : list){
            ZSetOperations.add(keyMap.get(tagCountEntity.getCategory()), tagCountEntity.getTag(), tagCountEntity.getCount());
        }

        log.info("Updated TagCount Table, reset Redis");
    }

    // 나눔 상태 변경
    @Override
    public void updateShareStatus(Long userPK, ShareStatusDTO shareStatusDTO) {
        ArticleEntity articleEntity = articleRepository.findById(shareStatusDTO.getArticleId()).orElseThrow();
        ArticleEntity newEntity = ArticleEntity.builder()
                .id(articleEntity.getId())
                .title(articleEntity.getTitle())
                .userEntity(userRepository.findById(userPK).orElseThrow())
                .category(articleEntity.getCategory())
                .content(articleEntity.getContent())
                .shareRegion(articleEntity.getShareRegion())
                .shareStatus(!articleEntity.getShareStatus())
                .views(articleEntity.getViews())
                .build();

        articleRepository.save(newEntity);
    }

    // 게시글 작성
    @Override
    public boolean createArticle(Long userPK, ArticleDTO articleDTO, String[] imgPaths) {
        String[] tags = articleDTO.getTags();
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();

        String key = keyMap.get(articleDTO.getCategory());

        if(tags != null){
            List<TagEntity> tagList = new ArrayList<>();

            for(String tag : tags) {
                // 태그가 redis에 추가, 증가
                ZSetOperations.incrementScore(key, tag, 1);

                // 태그테이블에 태그 insert
                if(tagRepository.findByName(tag) == null){
                    TagEntity tagEntity = TagEntity.builder()
                            .name(tag)
                            .build();
                    tagList.add(tagEntity);
                }
            }
            tagRepository.saveAll(tagList);
        }


        // article 테이블에 insert
        ArticleEntity articleEntity = ArticleEntity.builder()
                .category(articleDTO.getCategory())
                .userEntity(userRepository.findById(userPK).orElseThrow())
                .title(articleDTO.getTitle())
                .content(articleDTO.getContent())
                .views(0L)
                .shareStatus(articleDTO.getShareStatus())
                .shareRegion(articleDTO.getShareRegion())
                .build();

        ArticleEntity savedArticleEntity = articleRepository.save(articleEntity);

        // 태크-게시물 테이블에 insert
        if(tags != null){
            List<ArticleTagEntity> articleTagEntityList = new ArrayList<>();
            for(String tag : articleDTO.getTags()){
                ArticleTagEntity articleTagEntity = ArticleTagEntity.builder()
                        .articleEntity(savedArticleEntity)
                        .tagEntity(tagRepository.findByName(tag))
                        .build();

                articleTagEntityList.add(articleTagEntity);
            }
            articleTagRepository.saveAll(articleTagEntityList);
        }

        // 이미지 테이블에 게시글PK + 이미지주소 insert
        if(imgPaths != null){
            List<ArticleImageEntity> articleImageEntityList = new ArrayList<>();
            for(String imgPath : imgPaths){
                ArticleImageEntity articleImageEntity = ArticleImageEntity.builder()
                        .articleEntity(savedArticleEntity)
                        .img(imgPath)
                        .build();

                articleImageEntityList.add(articleImageEntity);
            }
            articleImageRepository.saveAll(articleImageEntityList);
        }
        return true;
    }

    @Override
    public ArticleResponseDTO readArticle(Long articleId, Long userPK) {
        // ArticleEntity 조회
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow();
        // UserEntity 조회
        UserEntity userEntity = userRepository.findById(articleEntity.getUserPK()).orElseThrow();

        // tags id 조회
        List<ArticleTagEntity> articleTagEntityList = articleTagRepository.findByArticleId(articleId);
        List<String> tags = new ArrayList<>();
        if(articleTagEntityList.size() != 0){
            // tag id로 tagEntity 조회
            for(ArticleTagEntity articleTagEntity : articleTagEntityList){
                tags.add(tagRepository.findById(articleTagEntity.getTagId()).orElseThrow().getName());
            }
        }

        // commentEntity 조회
        List<CommentEntity> commentEntityList = commentRepository.findByArticleId(articleId);

        List<CommentResponseDTO> comments = new ArrayList<>();
        int commentCnt = 0;
        // commentDTO build
        if(commentEntityList.size() != 0){
            for(CommentEntity commentEntity : commentEntityList){
                UserEntity commentUserEntity = userRepository.findById(commentEntity.getUserPK()).orElseThrow();
                CommentResponseDTO commentDTO = CommentResponseDTO.builder()
                        .id(commentEntity.getId())
                        .userPK(commentEntity.getUserPK())
                        .profile(commentUserEntity.getProfile() == null ? "" : commentUserEntity.getProfile())
                        .content(commentEntity.getContent())
                        .createTime(commentEntity.getCreatedDate())
                        .build();

                comments.add(commentDTO);
            }
            commentCnt = comments.size();
        }

        // bookmark 여부 조회
        // 복합키 사용을 위한 id 등록
        ArticleBookmarkEntityPK articleBookmarkEntityPK = new ArticleBookmarkEntityPK();
        articleBookmarkEntityPK.setUserEntity(userPK);
        articleBookmarkEntityPK.setArticleEntity(articleId);

        boolean bookmark;
        if(aBookmarkRepo.findById(articleBookmarkEntityPK).isPresent()){
            bookmark = true;
        }else bookmark = false;

        // image 조회
        List<String> imgPaths = new ArrayList<>();
        List<ArticleImageEntity> articleImageEntityList = articleImageRepository.findAllByArticleId(articleId);
        if(articleImageEntityList.size() != 0){
            for(ArticleImageEntity entity : articleImageEntityList){
                imgPaths.add(entity.getImg());
            }
        }

        ArticleResponseDTO articleResponseDTO = ArticleResponseDTO.builder()
                .category(articleEntity.getCategory())
                .imgs(imgPaths)
                .userPK(articleEntity.getUserPK())
                .nickName(userEntity.getNickName())
                .profile(userEntity.getProfile() == null ? "" : userEntity.getProfile())
                .title(articleEntity.getTitle())
                .tags(tags)
                .views(articleEntity.getViews()+1)
                .commentCnt(commentCnt)
                .bookmark(bookmark)
                .shareRegion(articleEntity.getShareRegion())
                .content(articleEntity.getContent())
                .shareStatus(articleEntity.getShareStatus())
                .createTime(articleEntity.getCreatedDate())
                .updateTime(articleEntity.getLastModifiedDate())
                .comments(comments)
                .build();

        // 조회수 업데이트
        ArticleEntity newArticleEntity = ArticleEntity.builder()
                .id(articleEntity.getId())
                .category(articleEntity.getCategory())
                .userEntity(userRepository.findById(articleEntity.getUserPK()).orElseThrow())
                .title(articleEntity.getTitle())
                .content(articleEntity.getContent())
                .views(articleEntity.getViews()+1)
                .shareStatus(articleEntity.getShareStatus())
                .shareRegion(articleEntity.getShareRegion())
                .build();

        articleRepository.save(newArticleEntity);

        return articleResponseDTO;
    }



    @Override
    public boolean updateArticle(ArticleDTO articleDTO, String[] imgPaths) {
        String[] tags = articleDTO.getTags();
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();

        String key = keyMap.get(articleDTO.getCategory());

        // 태그 처리
        if(tags != null){
            List<TagEntity> tagList = new ArrayList<>();

            for(String tag : tags) {
                // 태그가 redis에 추가, 증가
                ZSetOperations.incrementScore(key, tag, 1);

                // 태그테이블에 태그 insert
                if(tagRepository.findByName(tag) == null){
                    TagEntity tagEntity = TagEntity.builder()
                            .name(tag)
                            .build();
                    tagList.add(tagEntity);
                }
            }
            tagRepository.saveAll(tagList);
        }

        // redis에서 기존 태그 1 감소
        List<ArticleTagEntity> entities = articleTagRepository.findByArticleId(articleDTO.getArticleId());
        if(entities.size() != 0){
            for(ArticleTagEntity entity : entities){
                String tag = tagRepository.findById(entity.getTagId()).get().getName();
                ZSetOperations.incrementScore(key, tag, -1);
            }
        }

        // 태크-게시물 테이블에 기존 태그 delete
        List<ArticleTagEntity> articleTagEntityList = articleTagRepository.findByArticleId(articleDTO.getArticleId());
        if(articleTagEntityList.size() != 0){
            articleTagRepository.deleteAll(articleTagEntityList);
        }

        // 이미지 테이블의 기존 정보 delete
        List<ArticleImageEntity> articleImageEntityList = articleImageRepository.findAllByArticleId(articleDTO.getArticleId());
        if(articleImageEntityList.size() != 0 ){
            articleImageRepository.deleteAll(articleImageEntityList);
        }

        // article 테이블에 update
        ArticleEntity articleEntity = articleRepository.findById(articleDTO.getArticleId()).orElseThrow();
        ArticleEntity newArticleEntity = ArticleEntity.builder()
                .id(articleDTO.getArticleId())
                .category(articleDTO.getCategory())
                .userEntity(userRepository.findById(articleDTO.getUserPK()).orElseThrow())
                .title(articleDTO.getTitle())
                .content(articleDTO.getContent())
                .views(articleEntity.getViews())
                .shareStatus(articleDTO.getShareStatus())
                .shareRegion(articleDTO.getShareRegion())
                .build();

        ArticleEntity savedArticleEntity = articleRepository.save(newArticleEntity);

        // 태그-게시물 테이블에 insert
        if(tags != null){
            List<ArticleTagEntity> list = new ArrayList<>();
            for(String tag : tags){
                ArticleTagEntity articleTagEntity = ArticleTagEntity.builder()
                        .articleEntity(articleRepository.findById(savedArticleEntity.getId()).orElseThrow())
                        .tagEntity(tagRepository.findByName(tag))
                        .build();

                list.add(articleTagEntity);
            }
            articleTagRepository.saveAll(list);
        }

        // 이미지 테이블에 insert
        if(imgPaths != null){
            List<ArticleImageEntity> list = new ArrayList<>();
            for(String imgPath : imgPaths){
                ArticleImageEntity articleImageEntity = ArticleImageEntity.builder()
                        .articleEntity(savedArticleEntity)
                        .img(imgPath)
                        .build();

                list.add(articleImageEntity);
            }
            articleImageRepository.saveAll(list);
        }

        // 기존 이미지 다시 insert
        String[] saveImagesList = articleDTO.getSaveImages();
        if(saveImagesList!= null){
            List<ArticleImageEntity> list = new ArrayList<>();
            for(String image : saveImagesList){
                ArticleImageEntity articleImageEntity = ArticleImageEntity.builder()
                        .articleEntity(savedArticleEntity)
                        .img(image)
                        .build();

                list.add(articleImageEntity);
            }
            articleImageRepository.saveAll(list);
        }

        return true;
    }

    @Override
    public void deleteArticle(Long userPK, Long articleId) {
        // 권한 체크
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow();

        if(articleEntity.getUserPK() != userPK) throw new AccessDeniedException("삭제 권한 없음");

        // s3 이미지 삭제
        List<ArticleImageEntity> articleImageEntityList = articleImageRepository.findAllByArticleId(articleId);
        if(articleImageEntityList != null){
            for(ArticleImageEntity entity : articleImageEntityList){
                s3Service.delete(entity.getImg());
            }
        }

        // redis에서 태그 삭제
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        String key = keyMap.get(articleEntity.getCategory());

        List<ArticleTagEntity> entities = articleTagRepository.findByArticleId(articleId);
        if(entities.size() != 0){
            for(ArticleTagEntity entity : entities){
                String tag = tagRepository.findById(entity.getTagId()).get().getName();
                ZSetOperations.incrementScore(key, tag, -1);
            }
        }

        articleRepository.deleteById(articleId);
    }

    @Override
    public Page<ArticleListDTO> readArticleList(String category, Long userPK, Integer page, Integer size) {
        // 카테고리에 해당하는 게시글 조회
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<ArticleEntity> articleEntities = articleRepository.findAllByCategory(category, pageRequest);
        Page<ArticleListDTO> result = toDtoList(articleEntities, userPK);
        return result;
    }

    @Override
    public void updateBookMark(BookmarkDTO bookmarkDTO) {
        Long articleId = bookmarkDTO.getArticleId();
        Long userPK = bookmarkDTO.getUserPK();
        Boolean bStatus = bookmarkDTO.getBookmarkStatus();

        // bookmark 여부 조회
        // 복합키 사용을 위한 id 등록
        ArticleBookmarkEntityPK aBookmarkEntityPK = new ArticleBookmarkEntityPK();
        aBookmarkEntityPK.setUserEntity(userPK);
        aBookmarkEntityPK.setArticleEntity(articleId);

        if(bStatus){
            // 해제
            if(aBookmarkRepo.findById(aBookmarkEntityPK).isPresent()){
                aBookmarkRepo.delete(aBookmarkRepo.findById(aBookmarkEntityPK).orElseThrow());
            }

        }else{
            // 등록
            if(!aBookmarkRepo.findById(aBookmarkEntityPK).isPresent()){
                ArticleBookmarkEntity aBookmarkEntity = ArticleBookmarkEntity.builder()
                        .articleEntity(articleRepository.findById(articleId).orElseThrow())
                        .userEntity(userRepository.findById(userPK).orElseThrow())
                        .build();
                aBookmarkRepo.save(aBookmarkEntity);
            }
        }

    }

    @Override
    public Page<ArticleListDTO> filterRegion(String[] region, Long userPK, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArticleEntity> articleEntities = articleRepository.filterRegion(region, pageRequest);
        if(articleEntities == null){
            return null;
        }
        Page<ArticleListDTO> result = toDtoList(articleEntities, userPK);
        return result;
    }

    @Override
    public Page<ArticleListDTO> searchArticle(String category, String[] region, String keyword, Long userPK,  Boolean shareStatus, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArticleEntity> articleEntities = articleRepository.search(category, region, keyword,  pageRequest, shareStatus);
        Page<ArticleListDTO> result = toDtoList(articleEntities, userPK);
        return result;
    }

    // 작성자가 나눔 중인 다른 나눔글
    @Override
    public List<UserSharedArticleDTO> readUserShared(Long articleId) {
        ArticleEntity articleEntity = articleRepository.findById(articleId).orElseThrow();
        Long userPK = articleEntity.getUserPK();

        List<ArticleEntity> articleEntityList = articleRepository.findUserSharedArticle(userPK, articleId);

        List<UserSharedArticleDTO> result = new ArrayList<>();
        if(articleEntityList.size() != 0){
            for(ArticleEntity entity : articleEntityList){
                // 이미지 조회
                List<ArticleImageEntity> aImageEntityList = articleImageRepository.findAllByArticleId(entity.getId());

                UserSharedArticleDTO dto = UserSharedArticleDTO.builder()
                        .articleId(entity.getId())
                        .userPK(entity.getUserPK())
                        .nickName(userRepository.findById(userPK).orElseThrow().getNickName())
                        .title(entity.getTitle())
                        .img((aImageEntityList == null || aImageEntityList.size() == 0) ? null : aImageEntityList.get(0).getImg())
                        .build();

                result.add(dto);
            }
        }

        return result;
    }

    // 마이페이지 - 유저 작성글 조회
    @Override
    public Page<ArticleListDTO> readUserArticles(Long userPK, Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArticleEntity> articleEntities = articleRepository.findAllByUserPK(userPK, pageRequest);
        Page<ArticleListDTO> result = toDtoList(articleEntities, userPK);
        return result;
    }

    // 마이페이지 - 유저 북마크 조회
    @Override
    public Page<ArticleListDTO> readUserBookmarks(Long userPK, Integer page, Integer size) {
        List<Long> bookmarkList = articleRepository.findBookmarkByUserPK(userPK);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<ArticleEntity> articleEntities = articleRepository.findAllById(bookmarkList, pageRequest);

        return toDtoList(articleEntities, userPK);
    }

    public Page<ArticleListDTO> toDtoList(Page<ArticleEntity> articleEntities, Long userPK){

        Page<ArticleListDTO> dtoList = articleEntities.map(a ->
                ArticleListDTO.builder()
                        .articleId(a.getId())
                        .category(a.getCategory())
                        .img(findImgByArticleEntity(a))
                        .userPK(a.getUserPK())
                        .nickName(findUserEntityByArticleEntity(a).getNickName())
                        .profile(findUserEntityByArticleEntity(a).getProfile() == null ? "" : findUserEntityByArticleEntity(a).getProfile())
                        .title(a.getTitle())
                        .tags(findTagsByArticleEntity(a))
                        .views(a.getViews())
                        .commentCnt(findCommentCntByArticleEntity(a))
                        .bookmark(findBookmarkByArticleEntity(a, userPK))
                        .shareRegion(a.getShareRegion())
                        .shareStatus(a.getShareStatus())
                        .createTime(a.getCreatedDate())
                        .updateTime(a.getLastModifiedDate())
                        .build());
        return dtoList;
    }

    // tag count 갱신 함수
    public void resetTagCount(){
        // article_tag 테이블에서 tag count 구하기
       List<Object[]> list = articleTagRepository.findCountByTag();

       // tag count 테이블 리셋
       tagCountRepository.deleteAllInBatch();
       List<TagCountEntity> counts = new ArrayList<>();
       for(Object[] object : list){
           TagCountEntity tagCountEntity = TagCountEntity.builder()
                   .tag(String.valueOf(object[0]))
                   .count(Double.parseDouble(String.valueOf(object[1])))
                   .category(String.valueOf(object[2]))
                   .build();

           counts.add(tagCountEntity);
       }
       tagCountRepository.saveAll(counts);
    }

    // 북마크 조회 함수
    public boolean findBookmarkByArticleEntity(ArticleEntity articleEntity, Long userPK){
        // 복합키 사용을 위한 id 등록
        ArticleBookmarkEntityPK articleBookmarkEntityPK = new ArticleBookmarkEntityPK();
        articleBookmarkEntityPK.setUserEntity(userPK);
        articleBookmarkEntityPK.setArticleEntity(articleEntity.getId());
        boolean bookmark;
        if(aBookmarkRepo.findById(articleBookmarkEntityPK).isPresent()){
            bookmark = true;
        }else bookmark = false;

        return bookmark;
    }

    // 댓글 수 조회 함수
    public int findCommentCntByArticleEntity(ArticleEntity articleEntity){
        // 댓글수 조회
        int commentCnt;
        List<CommentEntity> commentEntityList = commentRepository.findByArticleId(articleEntity.getId());
        if(commentEntityList == null){
            commentCnt = 0;
        }else commentCnt = commentEntityList.size();

        return commentCnt;
    }


    // 태그 조회 함수
    public List<String> findTagsByArticleEntity(ArticleEntity articleEntity){
        List<String> tags = new ArrayList<>();
        List<ArticleTagEntity> articleTagEntityList = articleTagRepository.findByArticleId(articleEntity.getId());

        if (articleTagEntityList.size() == 0) return tags;

        for(ArticleTagEntity entity : articleTagEntityList){
            tags.add(tagRepository.findById(entity.getTagId()).orElseThrow().getName());
        }
        return tags;
    }


    // 게시글 entity로 작성자 entity 반환
    public UserEntity findUserEntityByArticleEntity(ArticleEntity articleEntity){
        return userRepository.findById(articleEntity.getUserPK()).orElseThrow();
    }

    // 이미지 조회 함수
    public String findImgByArticleEntity (ArticleEntity articleEntity){
        // 이미지 조회
        List<ArticleImageEntity> articleImageEntityList = articleImageRepository.findAllByArticleId(articleEntity.getId());
        String imgPath = null;
        if(articleImageEntityList != null && articleImageEntityList.size() !=0){
            // 첫번째 이미지 가져오기
            imgPath = articleImageEntityList.get(0).getImg();
        }
        return imgPath;
    }
}
