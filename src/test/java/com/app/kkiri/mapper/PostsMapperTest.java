package com.app.kkiri.mapper;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostFilterDTO;
import com.app.kkiri.domain.vo.PostVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostsMapperTest {
    @Autowired
    private PostsMapper postsMapper;

    @Test
    void insert() {
        PostDTO postDTO = new PostDTO();
        postDTO.create(3L,"test", "test", null, null,  "카페",231.2, 232.3, "2023-11-12", "2023-11-15");
        postDTO.setUserId(2L);
        postsMapper.insert(postDTO);
    }

    @Test
    void delete() {
        postsMapper.delete(3L);
    }

    @Test
    void update() {
        PostDTO postDTO = new PostDTO();
        postDTO.create(4L,"수정", "수정", null, null,  "카페",231.2, 232.3,"2023-10-30", "2023-11-05");
        postDTO.setPostId(2L);
        postsMapper.update(postDTO);
    }

    @Test
    void selectById() {
        postsMapper.selectById(2L);
    }

    @Test
    void selectByFilter(){
        Map<String, Object> param = new HashMap<>();
        List<Long> tagId = new ArrayList<>();
//        tagId.add(61L);

        param.put("spaceId", 4L);
        param.put("keyword", "test");
//        param.put("tags", tagId);
        param.put("amount", 10);
        param.put("page", 1);
        postsMapper.selectByFilter(param);
    }

    @Test
    void getTotal(){
        Map<String, Object> param = new HashMap<>();
        List<Long> tagId = new ArrayList<>();
//        tagId.add(61L);

//        param.put("tags", tagId);
        param.put("spaceId", 4L);
        param.put("keyword", "test");
        param.put("amount", 10);
        param.put("page", 1);
        postsMapper.getTotal(param);
    }
}