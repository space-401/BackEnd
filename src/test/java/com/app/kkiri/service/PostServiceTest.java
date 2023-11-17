package com.app.kkiri.service;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.vo.PostImgVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Test
    void register() {
        PostDTO postDTO = new PostDTO();
        List<PostImgVO> imgs = new ArrayList<>();
        PostImgVO postImgVO = new PostImgVO();
        List<Long> tags = new ArrayList<>();
        List<Long> people = new ArrayList<>();
        people.add(2L);
        people.add(3L);
        tags.add(62L);
        tags.add(63L);

        postImgVO.create("test", "test", "test", 12L, 0L);
        imgs.add(postImgVO);
        postDTO.create(46L,"serviceTest", "serviceTest", people, tags,  "카페",231.2, 232.3, "2023/10/30", "2023/11/05");
        postDTO.setUserId(1L);
        postService.register(postDTO, imgs);
    }

    @Test
    void remove() {
        postService.remove(43L);
    }

    @Test
    void modify() {
        PostDTO postDTO = new PostDTO();
        List<PostImgVO> imgs = new ArrayList<>();
        PostImgVO postImgVO = new PostImgVO();
        List<Long> tags = new ArrayList<>();
        List<Long> people = new ArrayList<>();
        postDTO.setPostId(44L);
        people.add(2L);
        people.add(3L);
        tags.add(61L);

        postImgVO.create("수정", "test", "test", 12L, 0L);
        imgs.add(postImgVO);
        postDTO.create(46L,"수정", "serviceTest", people, tags,  "카페",231.2, 232.3, "2023/10/30", "2023/11/05");
        postDTO.setUserId(1L);
        postService.modify(postDTO, imgs);
    }

    @Test
    void postDetail() {
        postService.postDetail(44L, 1L);
    }

    @Test
    void bookmark() {
        postService.bookmark(44L, 1L);
    }
}