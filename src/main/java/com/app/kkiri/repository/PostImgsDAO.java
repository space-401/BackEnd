package com.app.kkiri.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.vo.PostImgVO;
import com.app.kkiri.mapper.PostImgsMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostImgsDAO {
    private final PostImgsMapper postImgsMapper;

    // 이미지 저장
    public void save(PostImgVO postImgVO){ postImgsMapper.insert(postImgVO);}

    // 이미지 조회
    public List<String> findById(Long postId){ return postImgsMapper.selectById(postId); }

    // 이미지 삭제
    public void delete(Long postId){ postImgsMapper.delete(postId); }

    // postId 를 사용하여 대표 이미지 한장을 조회
    public String findByPostId(Long postId) {
        return postImgsMapper.selectOne(postId);
    }
}
