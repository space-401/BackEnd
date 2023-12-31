package com.app.kkiri.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.mapper.MentionMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MentionDAO {
    private final MentionMapper mentionMapper;

    // 멘션 추가
    public void insert(Long postId, Long uesrId){ mentionMapper.insert(postId, uesrId); }

    // 멘션 조회
    public List<SpaceUserVO> selectById(Long postId, Long spaceId){ return mentionMapper.selectById(postId, spaceId); }

    // 멘션 삭제
    public void delete(Long postId){ mentionMapper.delete(postId); }
}
