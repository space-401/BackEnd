package com.app.kkiri.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.app.kkiri.domain.vo.TagVO;

import lombok.extern.slf4j.Slf4j;

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
        tagVO.create("데이트", 44L);
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