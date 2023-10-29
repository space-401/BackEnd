package com.app.kkiri.controller;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostDetailDTO;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post/*")
@Slf4j
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping("/")
    public void register(@RequestBody PostDTO postDTO){}

    // 게시글 삭제
    @DeleteMapping("/")
    public void remove(@RequestParam Long postId){}

    // 게시글 수정
    @PatchMapping("/")
    public void modify(@RequestBody PostDTO postDTO){}

    // 게시글 상세 조회
    @GetMapping("/")
    public void postDetail(@RequestParam Long postId){}

    // 게시글 북마크
    @PostMapping("/bookmark")
    public ResponseEntity<?> bookmark(@RequestBody Long postId){
        Long userId = 1L;
        postService.bookmark(postId, userId);
        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }
}
