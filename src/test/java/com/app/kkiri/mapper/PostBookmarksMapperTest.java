package com.app.kkiri.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostBookmarksMapperTest {
    @Autowired
    private PostBookmarksMapper postBookmarksMapper;

    @Test
    void updateBookmark() {
        postBookmarksMapper.updateBookmark(46L, 1L);
    }

    @Test
    void delete(){
        postBookmarksMapper.delete(46L, 1L);
    }
}