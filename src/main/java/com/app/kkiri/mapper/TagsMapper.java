package com.app.kkiri.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.vo.TagVO;

@Mapper
public interface TagsMapper {
	// 스페이스 태그 목록
	public List<TagVO> selectAll(Long spaceId);

	// 스페이스 태그 추가
	public void insert(TagVO tagVO);

	// 스페이스 태그 개수
	public int getTagCnt(Long spaceId);

	// 스페이스 태그 삭제
	public void delete(Long tagId);
}
