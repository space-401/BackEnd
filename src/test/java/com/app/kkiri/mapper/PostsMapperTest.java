package com.app.kkiri.mapper;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.vo.PostVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostsMapperTest {
    @Autowired
    private PostsMapper postsMapper;

    @Test
    void insert() {
        PostDTO postDTO = new PostDTO();
        postDTO.create(46L,"test", "test", null, null,  "카페",231.2, 232.3, "2023/10/30", "2023/11/01");
        postDTO.setUserId(1L);
        postsMapper.insert(postDTO);
    }

    @Test
    void delete() {
        postsMapper.delete(3L);
    }

    @Test
    void update() {
        PostDTO postDTO = new PostDTO();
        postDTO.create(46L,"수정", "수정", null, null,  "카페",231.2, 232.3,"2023/10/30", "2023/11/05");
        postDTO.setPostId(4L);
        postsMapper.update(postDTO);
    }

    @Test
    void selectById() {
        postsMapper.selectById(5L);
    }
}