package com.app.kkiri.mapper;

import com.app.kkiri.domain.dto.SpaceDetailUserDTO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MentionMapper {
    // 멘션 추가
    public void insert(Long postId, Long userId);

    // 멘션 조회
    public List<SpaceUserVO> selectById(Long postId, Long spaceId);

    // 멘션 삭제
    public void delete(Long postId);
}
