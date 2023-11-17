package com.app.kkiri.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MentionMapperTest {
    @Autowired
    private MentionMapper mentionMapper;

    @Test
    void insert() {
        mentionMapper.insert(4L, 3L);
    }

    @Test
    void selectById() {
        mentionMapper.selectById(4L, 46L);
    }

    @Test
    void delete() {
        mentionMapper.delete(46L);
    }
}