package com.app.kkiri.service;

import com.app.kkiri.domain.dto.CommentDTO;
import com.app.kkiri.domain.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Test
    void register() {
        CommentVO commentVO = new CommentVO();
        commentVO.create("댓글4", false, 44L, 1L);
        commentService.register(commentVO);
    }

    @Test
    void remove() {
        commentService.remove(8L);
    }

    @Test
    void list() {
        List<CommentDTO> list = new ArrayList<>();
        list= commentService.list(44L, 46L);
        log.info("list: " + list);
    }
}