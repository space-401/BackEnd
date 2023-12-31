package com.app.kkiri.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.dto.response.TagResponseDTO;
import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.mapper.TagsMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TagsDAO {
	private final TagsMapper tagsMapper;
	// 스페이스 태그 목록
	public List<TagVO> findAll(Long spaceId){ return tagsMapper.selectAll(spaceId); }

	// 스페이스 태그 추가
	public void save(TagVO tagVO){ tagsMapper.insert(tagVO); }

	// 스페이스 태그 개수
	public int getTagCnt(Long spaceId){ return tagsMapper.getTagCnt(spaceId); }

	// 스페이스 태그 삭제
	public void delete(Long tagId){ tagsMapper.delete(tagId); }

	// 가장 최근에 생성된 태그 조회
	public TagResponseDTO findRecentTag() {
		TagVO selectedRecentTag = tagsMapper.selectRecentTag();

		TagResponseDTO tagResponseDTO = TagResponseDTO.builder()
			.tagId(selectedRecentTag.getTagId())
			.tagName(selectedRecentTag.getTagName())
			.build();

		return tagResponseDTO;
	}
}
