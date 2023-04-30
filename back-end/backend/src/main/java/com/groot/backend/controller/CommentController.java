package com.groot.backend.controller;

import com.groot.backend.dto.request.CommentDTO;
import com.groot.backend.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/comments")
public class CommentController {
    @PostMapping
    public ResponseEntity insertComment(@RequestBody CommentDTO commentDTO, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);

    }

     @DeleteMapping("/{commentID}/{userPK}")
    public ResponseEntity deleteComment(@PathVariable Long commentId, @PathVariable Long userPK, HttpServletRequest request){
        Map resultMap = new HashMap();
        Long userId = JwtTokenProvider.getIdByAccessToken(request);
     }
}
