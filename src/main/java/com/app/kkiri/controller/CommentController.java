package com.app.kkiri.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

    private Long getUserId(HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);

        if(token == null) { // 헤더 이상
            // throw new CustomException(StatusCode.INSUFFICIENT_HEADER);
        }

        Long userId = jwtTokenProvider.getUserIdByToken(token);

        if(userId == null) { // 만료되거나 이상이 있는 토큰
            // throw new CustomException(StatusCode.INVALID_TOKEN);
        }

        return userId;
    }

    @PostMapping("")
    // 댓글 추가
    public ResponseEntity<?> insert(@RequestBody CommentVO commentVO, HttpServletRequest request) {
        Long userId = getUserId(request);
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
    public ResponseEntity<?> list(@RequestParam Long postId, @RequestParam Long spaceId){
        List<CommentResponseDTO> commentList = commentService.list(postId, spaceId);

        return ResponseEntity.ok().body(commentList);

    }
}
