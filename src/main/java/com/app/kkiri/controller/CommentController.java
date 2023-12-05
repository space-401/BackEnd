package com.app.kkiri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.kkiri.domain.dto.response.CommentResponseDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @PostMapping("")
    // 댓글 추가
    public ResponseEntity<?> insert(@RequestBody CommentVO commentVO, HttpServletRequest request) {
        LOGGER.info("[insert()] param commentVO : {}", commentVO);

        Long userId = jwtTokenProvider.getUserIdByHttpRequest(request);

        commentVO.setUserId(userId);

        commentService.register(commentVO);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("")
    // 댓글 삭제
    public ResponseEntity<?> delete(@RequestParam Long commentId){
        commentService.remove(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("")
    // 댓글 조회
    public ResponseEntity<?> list(@RequestParam Long postId, @RequestParam Long spaceId, HttpServletRequest request){
        Long userId = jwtTokenProvider.getUserIdByHttpRequest(request);
        List<CommentResponseDTO> commentList = commentService.list(postId, spaceId, userId);

        return ResponseEntity.ok().body(commentList);

    }
}
