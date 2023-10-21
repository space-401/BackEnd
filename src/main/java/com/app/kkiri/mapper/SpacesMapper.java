package com.app.kkiri.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.vo.SpaceVO;

@Mapper
public interface SpacesMapper {
	// 목록 조회
	public List<SpaceVO> selectAll(Long userId);

	// 스페이스 상세 조회
	public SpaceVO selectById(Long spaceId);

	// 스페이스 입장
	public Long selectByCodeAndPw(SpaceVO spaceVO);

	// 스페이스 생성
	public void insert(SpaceVO spaceVO);

	// 스페이스 삭제
	public void delete(Long spaceId);

	// 스페이스 수정
	public void update(SpaceVO spaceVO);

	// 스페이스 회원 전체수
	 public int getTally(Long spaceId);

	// 스페이스 인원 업데이트
	public void updateTally(SpaceVO spaceVO);
}
