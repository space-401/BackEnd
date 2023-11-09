package com.app.kkiri.controller;

import com.app.kkiri.domain.dto.CommentResponseDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    private Long getUserId(HttpServletRequest request){
        try{
            String token = jwtTokenProvider.resolveToken(request);
            return  jwtTokenProvider.getUserId(token);
        } catch (Exception e){
            throw new CustomException(StatusCode.INVALID_TOKEN);
        }
    }

    @PostMapping("")
    // 댓글 추가
    public ResponseEntity<?> insert(@RequestBody CommentVO commentVO, HttpServletRequest request) {
        Long userId = getUserId(request);
        commentVO.setUserId(userId);
        commentService.register(commentVO);
        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }

    @DeleteMapping("")
    // 댓글 삭제
    public ResponseEntity<?> delete(@RequestParam Long commentId){
        commentService.remove(commentId);
        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }

    @GetMapping("")
    // 댓글 조회
    public ResponseEntity<?> list(@RequestParam Long postId, @RequestParam Long spaceId){
        List<CommentResponseDTO> commentList = commentService.list(postId, spaceId);

        return ResponseEntity.ok().body(commentList);

    }
}
