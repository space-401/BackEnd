package com.app.kkiri.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostTagsMapperTest {
    @Autowired
    private PostTagsMapper postTagsMapper;

    @Test
    void insert() {
        postTagsMapper.insert(4L, 63L);
    }

    @Test
    void delete() {
        postTagsMapper.delete(4L);
    }

    @Test
    void selectById() {
        postTagsMapper.selectById(4L);
    }
}