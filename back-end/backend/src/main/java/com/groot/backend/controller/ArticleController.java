package com.groot.backend.controller;

import com.groot.backend.dto.request.ArticleDTO;
import com.groot.backend.dto.request.BookmarkDTO;
import com.groot.backend.dto.response.ArticleListDTO;
import com.groot.backend.dto.response.ArticleResponseDTO;
import com.groot.backend.service.ArticleService;
import com.groot.backend.service.S3Service;
import com.groot.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/articles")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private final S3Service s3Service;
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static Map<String, Object> resultMap = null;

    // 게시글 작성
    @PostMapping()
    public ResponseEntity createArticle(@RequestPart(value = "images", required = false) MultipartFile[] images,
           @Valid @RequestPart(value = "articleDTO") ArticleDTO articleDTO) throws Exception {
        resultMap = new HashMap<>();
        if(!userService.isExistedId(articleDTO.getUserPK())){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 사용자입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        // 이미지 업로드
        String[] imgPaths = null;
        if(images != null) {
            imgPaths = s3Service.upload(images, "article");
        }

        if(!articleService.createArticle(articleDTO, imgPaths)){
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시물 등록 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }

        resultMap.put("result", SUCCESS);
        resultMap.put("msg","게시물이 등록되었습니다.");
        return ResponseEntity.ok().body(resultMap);
    }


    // 카테고리별 게시글 리스트 조회
    @GetMapping("/category/{category}")
    public ResponseEntity readArticleList(@PathVariable String category, @RequestParam Integer page, @RequestParam Integer size){
        resultMap = new HashMap<>();

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
            Page<ArticleListDTO> result = articleService.readArticleList(category, page, size);
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
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 게시글 수정
    @PutMapping()
    public ResponseEntity updateArticle(@RequestPart(value = "images", required = false) MultipartFile[] images,
                                        @Valid @RequestPart(value = "articleDTO") ArticleDTO articleDTO) throws Exception {
        resultMap = new HashMap<>();

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
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 수정 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }


    }

    // 개별 게시글 조회
    @GetMapping("/{articleId}")
    public ResponseEntity readArticle(@PathVariable Long articleId){
        resultMap = new HashMap<>();
        if(!articleService.existedArticleId(articleId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            ArticleResponseDTO articleResponseDTO = articleService.readArticle(articleId);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시물이 조회되었습니다.");
            resultMap.put("article",articleResponseDTO);
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 조회 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity deleteArticle(@PathVariable Long articleId){
        resultMap = new HashMap<>();
        if(!articleService.existedArticleId(articleId)){
            resultMap.put("result", FAIL);
            resultMap.put("msg","존재하지 않는 게시글입니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            articleService.deleteArticle(articleId);
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시글이 삭제되었습니다.");
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 삭제 실패.");
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
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

    // 게시글 검색 (제목 검색)
    @GetMapping("/search/{category}/{keyword}")
    public ResponseEntity searchArticle(@PathVariable String category, @PathVariable String keyword){
        return null;
    }

    // 인기태그 조회

    // 나눔 게시글 지역 필터링
    @GetMapping("/filter")
    public ResponseEntity regionFilteredArticle(@RequestParam String[] region,
                                                @RequestParam Integer page,
                                                @RequestParam Integer size){
        resultMap = new HashMap<>();

        if(size == 0){
            resultMap.put("result", FAIL);
            resultMap.put("msg","size값은 1 이상이어야 합니다.");
            return ResponseEntity.badRequest().body(resultMap);
        }

        try{
            Page<ArticleListDTO> result = articleService.filterRegion(region, page, size);
            if(result == null){
                resultMap.put("result", SUCCESS);
                resultMap.put("msg","페이지에 해당하는 게시글이 존재하지 않습니다.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(resultMap);
            }
            resultMap.put("result", SUCCESS);
            resultMap.put("msg","게시글 조회 성공");
            resultMap.put("articles", articleService.filterRegion(region, page, size));
            return ResponseEntity.ok().body(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            resultMap.put("result", FAIL);
            resultMap.put("msg","게시글 조회 실패");
            return ResponseEntity.internalServerError().body(resultMap);
        }

    }

    // 댓글 작성

    // 댓글 수정

    // 댓글 삭제

    // 사용자가 나눔 중인 다른 식물 조회

    // 태그 자동완성



}
