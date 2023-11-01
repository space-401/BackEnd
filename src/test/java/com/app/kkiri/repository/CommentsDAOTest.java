package com.app.kkiri.repository;

import com.app.kkiri.domain.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CommentsDAOTest {
    @Autowired
    private CommentsDAO commentsDAO;

    @Test
    void save() {
        CommentVO commentVO = new CommentVO();
        commentVO.create("댓글3", false,  44L, 1L);
        commentsDAO.save(commentVO);
    }

    @Test
    void saveReply() {
        CommentVO commentVO = new CommentVO();
        commentVO.create("댓글3-1", true, 3L,44L, 1L);
        commentsDAO.saveReply(commentVO);

    }

    @Test
    void delete() {
        commentsDAO.delete(6L);
    }

    @Test
    void findById() {
        commentsDAO.findById(44L);
    }
}