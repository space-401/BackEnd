package com.app.kkiri.mapper;

import com.app.kkiri.domain.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CommentsMapperTest {
    @Autowired
    private CommentsMapper commentsMapper;

    @Test
    void insert() {
        CommentVO commentVO = new CommentVO();
//        commentVO.create("댓글2", false,  44L, 1L);
        commentVO.create("댓글2-1", true, 3L,44L, 1L);
        if(commentVO.getCommentRefYn()){
            commentsMapper.insertReply(commentVO);
        } else {
            commentsMapper.insert(commentVO);
        }
    }

    @Test
    void delete() {
        commentsMapper.delete(2L);
    }

    @Test
    void selectById() {
        commentsMapper.selectById(44L);
    }
}