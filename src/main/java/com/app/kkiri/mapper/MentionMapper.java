package com.app.kkiri.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.vo.SpaceUserVO;

@Mapper
public interface MentionMapper {
    // 멘션 추가
    public void insert(Long postId, Long userId);

    // 멘션 조회
    public List<SpaceUserVO> selectById(Long postId, Long spaceId);

    // 멘션 삭제
    public void delete(Long postId);
}
