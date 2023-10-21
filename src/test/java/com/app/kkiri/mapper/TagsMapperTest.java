package com.app.kkiri.mapper;

import com.app.kkiri.domain.vo.TagVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TagsMapperTest {
    @Autowired
    private TagsMapper tagsMapper;

    @Test
    void selectAll() {
        tagsMapper.selectAll(4L);
    }

    @Test
    void insert() {
        TagVO tagVO = new TagVO();
        tagVO.create("정기모임", 41L);
        tagsMapper.insert(tagVO);
    }

    @Test
    void getTagCnt() {
        tagsMapper.getTagCnt(4L);
    }

    @Test
    void delete() {
        tagsMapper.delete(9L);
    }
}