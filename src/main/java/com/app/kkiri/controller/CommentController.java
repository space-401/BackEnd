package com.app.kkiri.controller;

import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment/*")
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("")
    // 댓글 추가
    public void insert(@RequestBody CommentVO commentVO) {

    }

    @DeleteMapping("")
    // 댓글 삭제
    public void delete(@RequestParam Long commentId){

    }

    @GetMapping("")
    // 댓글 조회
    public void selectById(@RequestParam Long postId, @RequestParam Long spaceId){

    }
}
