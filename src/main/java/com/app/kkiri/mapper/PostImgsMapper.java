package com.app.kkiri.mapper;

import com.app.kkiri.domain.vo.PostImgVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostImgsMapper {
    // 이미지 저장
    public void insert(PostImgVO postImgVO);

    // 이미지 조회
    public List<String> selectById(Long postId);

    // 이미지 삭제
    public void delete(Long postId);
}
