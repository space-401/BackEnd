package com.app.kkiri.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.vo.PostImgVO;

@Mapper
public interface PostImgsMapper {
    // 이미지 저장
    public void insert(PostImgVO postImgVO);

    // 이미지 조회
    public List<String> selectById(Long postId);

    // 이미지 삭제
    public void delete(Long postId);

    // postId 를 사용하여 대표 이미지 한장을 조회
    public String selectOne(Long postId);
}
