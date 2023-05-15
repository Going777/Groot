package com.groot.backend.controller;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.request.BookmarkDTO;
import com.groot.backend.dto.request.ShareStatusDTO;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import com.groot.backend.dto.response.TagRankDTO;
import com.groot.backend.dto.response.UserSharedArticleDTO;
import com.groot.backend.service.ArticleService;
import com.groot.backend.service.S3Service;
import com.groot.backend.service.UserService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ArticleController {
    private final ArticleService articleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final S3Service s3Service;
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static Map<String, Object> resultMap = null;

    // 게시글 작성
    @PostMapping()
    public ResponseEntity createArticle(HttpServletRequest request,
                                        @RequestPart(value = "images", required = false) MultipartFile[] images,
           @Valid @RequestPart(value = "articleDTO") ArticleDTO articleDTO) throws Exception {
        resultMap = new HashMap<>();

        if(request.getHeader("Authorization") == null) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        if(!userService.isExistedId(articleDTO.getUserPK())){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 카테고리 확인
        String category = articleDTO.getCategory();
        if(!(category.equals("나눔") || category.equals("자유") || category.equals("QnA") || category.equals("Tip"))){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 카테고리입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 나눔일 경우 shareState와 shareRegion 존재 여부 확인
        if(category.equals("나눔")){
            if((articleDTO.getShareStatus() == null || articleDTO.getShareRegion() == null || articleDTO.getShareRegion().equals(""))){
                resultMap.put("result", FAIL);
                resultMap.put("msg","shareStatus 또는 shareRegion 값이 존재하지 않습니다.");
                return ResponseEntity.badRequest().body(resultMap);
            }
        }

        // title, content 확인
        if(articleDTO.getTitle().equals("") || articleDTO.getContent().equals("")){
            resultMap.put("result", FAIL);
            resultMap.put("msg","title 또는 content 내용이 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        Long userPK = jwtTokenProvider.getIdByAccessToken(request);
        if(userPK != articleDTO.getUserPK()){
            resultMap.put("result", FAIL);
            resultMap.put("msg","수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resultMap);
        }

        // 이미지 업로드
        String[] imgPaths = null;
        if(images != null) {
            imgPaths = s3Service.upload(images, "article");
        }

        try {
            articleService.createArticle(userPK, articleDTO, imgPaths);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시물이 등록되었습니다.");
            return ResponseEntity.ok().body(resultMap);

        }catch (RedisConnectionFailureException e){
            resultMap.put("result", FAIL);
            resultMap.put("msg","redis 연결 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        } catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시물 등록 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }


    // 카테고리별 게시글 리스트 조회
    @GetMapping("/category/{category}")
    public ResponseEntity readArticleList(HttpServletRequest request,
                                          @PathVariable String category, @RequestParam Integer page, @RequestParam Integer size){
        resultMap = new HashMap<>();
        Long userPK = jwtTokenProvider.getIdByAccessToken(request);


        if(size == 0){
            resultMap.put("result", FAIL);
            resultMap.put("msg","size값은 1 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if(!(category.equals("나눔") || category.equals("자유") || category.equals("QnA") || category.equals("Tip"))){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 카테고리입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            Page<ArticleListDTO> result = articleService.readArticleList(category, userPK, page, size);
            if(result == null){
                resultMap.put("result", FAIL);
                resultMap.put("msg","존재하지 않는 페이지번호 입니다.");
                return ResponseEntity.badRequest().body(resultMap);
            }
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시글 목록 조회 완료");
            resultMap.put("articles", result);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시물 목록 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 게시글 수정
    @PutMapping()
    public ResponseEntity updateArticle(HttpServletRequest request,
                                        @RequestPart(value = "images", required = false) MultipartFile[] images,
                                        @Valid @RequestPart(value = "articleDTO") ArticleDTO articleDTO) throws Exception {
        resultMap = new HashMap<>();

        if(request.getHeader("Authorization") == null) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        // 게시글 존재 여부 확인
        if(!articleService.existedArticleId(articleDTO.getArticleId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if(!userService.isExistedId(articleDTO.getUserPK())){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        Long userPK = jwtTokenProvider.getIdByAccessToken(request);
        if(userPK != articleDTO.getUserPK()){
            resultMap.put("result", FAIL);
            resultMap.put("msg","수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resultMap);
        }

        // 카테고리 확인
        String category = articleDTO.getCategory();
        if(!(category.equals("나눔") || category.equals("자유") || category.equals("QnA") || category.equals("Tip"))){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 카테고리입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 나눔일 경우 shareState와 shareRegion 존재 여부 확인
        if(category.equals("나눔")){
            if((articleDTO.getShareStatus() == null || articleDTO.getShareRegion() == null || articleDTO.getShareRegion().equals(""))){
                resultMap.put("result", FAIL);
                resultMap.put("msg","shareStatus 또는 shareRegion 값이 존재하지 않습니다.");
                return ResponseEntity.badRequest().body(resultMap);
            }
        }

        // 새 이미지 업로드
        String[] imgPaths = null;
        if(images != null) {
            imgPaths = s3Service.upload(images, "article");
        }

        try{
            articleService.updateArticle(articleDTO, imgPaths);

            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시글이 수정되었습니다.");
            return ResponseEntity.ok().body(resultMap);
        }catch (RedisConnectionFailureException e){
            resultMap.put("result", FAIL);
            resultMap.put("msg","redis 연결 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        } catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 수정 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 개별 게시글 조회
    @GetMapping("/{articleId}")
    public ResponseEntity readArticle(HttpServletRequest request,
                                      @PathVariable Long articleId){
        Long userPK = jwtTokenProvider.getIdByAccessToken(request);

        resultMap = new HashMap<>();
        if(!articleService.existedArticleId(articleId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            ArticleResponseDTO articleResponseDTO = articleService.readArticle(articleId, userPK);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시물이 조회되었습니다.");
            resultMap.put("article",articleResponseDTO);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity deleteArticle(HttpServletRequest request,
                                        @PathVariable Long articleId) {
        resultMap = new HashMap<>();

        if (!articleService.existedArticleId(articleId)) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if (request.getHeader("Authorization") == null) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        try {
            Long userPK = jwtTokenProvider.getIdByAccessToken(request);
            articleService.deleteArticle(userPK, articleId);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg", "게시글이 삭제되었습니다.");
            return ResponseEntity.ok().body(resultMap);
        } catch (AccessDeniedException e) {
            resultMap.put("result", FAIL);
            resultMap.put("msg","삭제 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resultMap);
        } catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 삭제 실패.");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 개별 게시글 북마크 등록/해제
    @PutMapping("/bookmark")
    public ResponseEntity updateBookMark(@RequestBody BookmarkDTO bookmarkDTO){
        resultMap = new HashMap<>();

        // 게시글 존재 여부 확인
        if(!articleService.existedArticleId(bookmarkDTO.getArticleId())){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if(!userService.isExistedId(bookmarkDTO.getUserPK())){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            // 북마크 수정
            articleService.updateBookMark(bookmarkDTO);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","북마크 수정 완료");
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","북마크 수정 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 게시글 검색 (지역+카테고리 필터링, 제목+내용+태그 검색)
    @GetMapping("/search")
    public ResponseEntity searchArticle(HttpServletRequest request,
                                        @RequestParam String[] region,
                                        @RequestParam String category,
                                        @RequestParam String keyword,
                                        @RequestParam(required = false) Boolean shareStatus,
                                        @RequestParam Integer page,
                                        @RequestParam Integer size){
        resultMap = new HashMap<>();
        Long userPK = jwtTokenProvider.getIdByAccessToken(request);


        if(size == 0){
            resultMap.put("result", FAIL);
            resultMap.put("msg","size값은 1 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        if(!(category.equals("나눔") || category.equals("자유") || category.equals("QnA") || category.equals("Tip"))){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 카테고리입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            Page<ArticleListDTO> result = articleService.searchArticle(category, region, keyword, userPK, shareStatus, page, size);
            if(result == null){
                resultMap.put("result", FAIL);
                resultMap.put("msg","존재하지 않는 page 번호 입니다.");
                return ResponseEntity.badRequest().body(resultMap);
            }
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시글 조회 성공");
            resultMap.put("articles", result);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 인기태그 조회
    @GetMapping("/tag")
    public ResponseEntity readTagRanking(){
        resultMap = new HashMap<>();
        try{
            List<TagRankDTO> result = articleService.readTagRanking();
            if(result.size() == 0){
                resultMap.put("result", SUCCESS);
                resultMap.put("msg","존재하는 태그가 없습니다.");
                resultMap.put("tags", result);
                return ResponseEntity.ok().body(resultMap);
            }
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","인기 태그 조회 성공");
            resultMap.put("tags", result);
            return ResponseEntity.ok().body(resultMap);
        }catch (RedisConnectionFailureException e){
            resultMap.put("result", FAIL);
            resultMap.put("msg","redis 연결 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        } catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","태그 랭킹 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }

    }

    // 나눔 게시글 지역 필터링
    @GetMapping("/filter")
    public ResponseEntity regionFilteredArticle(HttpServletRequest request,
                                                @RequestParam String[] region,
                                                @RequestParam Integer page,
                                                @RequestParam Integer size){
        resultMap = new HashMap<>();
        Long userPK = jwtTokenProvider.getIdByAccessToken(request);


        if(size == 0){
            resultMap.put("result", FAIL);
            resultMap.put("msg","size값은 1 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            Page<ArticleListDTO> result = articleService.filterRegion(region, userPK, page, size);
            if(result == null){
                resultMap.put("result", FAIL);
                resultMap.put("msg","존재하지 않는 page 번호 입니다.");
                return ResponseEntity.badRequest().body(resultMap);
            }
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시글 조회 성공");
            resultMap.put("articles", result);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }

    }

    // 사용자가 나눔 중인 다른 식물 조회
    @GetMapping("/share/{articleId}")
    public ResponseEntity readUserShared(@PathVariable Long articleId){
        resultMap = new HashMap<>();
        if(!articleService.existedArticleId(articleId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            List<UserSharedArticleDTO> result = articleService.readUserShared(articleId);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","나눔 목록이 조회되었습니다.");
            resultMap.put("articles", result);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 지역 반환
    @GetMapping("/regions/list")
    public ResponseEntity readRegion(){
        resultMap = new HashMap<>();
        try{
            List<String> result = articleService.readRegion();

            resultMap.put("result", SUCCESS);
            resultMap.put("msg","지역 리스트 조회");
            resultMap.put("regions", result);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","지역 리스트 조회 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    @PutMapping("/shareStatus")
    public ResponseEntity updateShareStatus(HttpServletRequest request,
                                            @RequestBody ShareStatusDTO shareStatusDTO){
        resultMap = new HashMap<>();
        // 작성자 확인
        if(request.getHeader("Authorization") == null) {
            resultMap.put("result", FAIL);
            resultMap.put("msg", "토큰이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultMap);
        }

        Long userPK = jwtTokenProvider.getIdByAccessToken(request);
        if(userPK != shareStatusDTO.getUserPK()){
            resultMap.put("result", FAIL);
            resultMap.put("msg","수정 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resultMap);
        }

        if(!articleService.existedArticleId(shareStatusDTO.getArticleId())) {
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            articleService.updateShareStatus(userPK, shareStatusDTO);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","나눔 상태 변경 완료");
            return ResponseEntity.ok().body(resultMap);

        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","나눔 상태 변경 실패");
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }

    }
}
