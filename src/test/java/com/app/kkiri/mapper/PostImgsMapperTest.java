package com.app.kkiri.mapper;

import com.app.kkiri.domain.vo.PostImgVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostImgsMapperTest {
    @Autowired
    private PostImgsMapper postImgsMapper;

    @Test
    void insert() {
        PostImgVO postImgVO = new PostImgVO();
        postImgVO.create("test", "test", "test", 12L, 4L);
        postImgsMapper.insert(postImgVO);
    }

    @Test
    void selectById() {
        postImgsMapper.selectById(4L);
    }

    @Test
    void delete() {
        postImgsMapper.delete(4L);
    }
}