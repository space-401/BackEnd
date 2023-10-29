package com.app.kkiri.service;

import com.app.kkiri.domain.dto.PostDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Test
    void register() {
//        PostDTO postDTO = new PostDTO();
//        postDTO.create(46L,"serviceTest", "serviceTest", null, null,  "카페",231.2, 232.3, null, "2023/10/30", "2023/11/05");
//        postService.register();
    }

    @Test
    void remove() {
    }

    @Test
    void modify() {
    }

    @Test
    void postDetail() {
    }

    @Test
    void bookmark() {
    }
}