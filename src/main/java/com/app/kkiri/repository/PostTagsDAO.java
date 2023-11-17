package com.app.kkiri.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.mapper.PostTagsMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostTagsDAO {
    private final PostTagsMapper postTagsMapper;
    // 태그 추가
    public void save(Long postId, Long tagId){ postTagsMapper.insert(postId, tagId); }

    // 태그 삭제
    public void delete(Long postId){ postTagsMapper.delete(postId); }

    // 태그 조회
    public List<TagVO> findById(Long postId){ return postTagsMapper.selectById(postId); }
}
