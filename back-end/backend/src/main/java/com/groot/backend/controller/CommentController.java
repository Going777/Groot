package com.groot.backend.controller;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.dto.response.CommentResponseDTO;
import com.groot.backend.entity.CommentEntity;
import com.groot.backend.service.CommentService;
import com.groot.backend.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private static String SUCCESS = "success";
    private static String FAIL = "fail";

    @PostMapping    // 댓글 작성
    public ResponseEntity insertComment(@RequestBody CommentDTO commentDTO, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        CommentEntity result = commentService.insertComment(commentDTO, userId);
        if(result==null){
            resultMap.put("msg", "댓글 작성을 실패하였습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("msg", "댓글 작성을 성공하였습니다.");
        resultMap.put("result", SUCCESS);
        resultMap.put("comment", new CommentResponseDTO().toDto(result));
        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping // 댓글 수정
    public ResponseEntity updateComment(@RequestBody CommentDTO commentDTO, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(userId != commentDTO.getUserPK()){
            resultMap.put("msg", "댓글 수정 권한이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        CommentEntity result = commentService.insertComment(commentDTO, userId);
        if(result==null){
            resultMap.put("msg", "댓글 수정을 실패하였습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("msg", "댓글 수정을 성공하였습니다.");
        resultMap.put("result", SUCCESS);
        resultMap.put("comment", new CommentResponseDTO().toDto(result));
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/{commentId}/{userPK}") // 댓글 삭제
    public ResponseEntity deleteComment(@PathVariable Long commentId, @PathVariable Long userPK, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
        if(userId != userPK){
            resultMap.put("msg", "댓글 삭제 권한이 없습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        if(!commentService.deleteComment(commentId)){
            resultMap.put("msg", "댓글이 존재하지 않습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
         resultMap.put("msg", "댓글 삭제를 성공하였습니다.");
         resultMap.put("result", SUCCESS);
         return ResponseEntity.ok().body(resultMap);
     }

    @GetMapping("/list/{articleId}") // 해당 게시글 댓글 리스트 조회
    public ResponseEntity readCommentList(@PathVariable Long articleId){
        Map<String, Object> resultMap = new HashMap();
        List<CommentResponseDTO> commentEntities = commentService.readComment(articleId);
        if(commentEntities==null){
            resultMap.put("msg", "댓글이 존재하지 않습니다.");
            resultMap.put("result", FAIL);
            return ResponseEntity.badRequest().body(resultMap);
        }
        resultMap.put("msg", "댓글 조회를 성공하였습니다.");
        resultMap.put("result", SUCCESS);
        resultMap.put("comment", commentEntities);
        return ResponseEntity.ok().body(resultMap);
    }
}
