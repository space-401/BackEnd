package com.app.kkiri.mapper;

import com.app.kkiri.domain.dto.TagDTO;
import com.app.kkiri.domain.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostTagsMapper {
    // 태그 추가
    public void insert(Long postId, Long tagId);

    // 태그 삭제
    public void delete(Long postId);

    // 태그 조회
    public List<TagVO> selectById(Long postId);
}
