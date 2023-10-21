package com.app.kkiri.repository;

import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.mapper.SpacesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpacesDAO {
	private final SpacesMapper spacesMapper;
	// 목록 조회
	public List<SpaceVO> findAll(Long userId){
		return spacesMapper.selectAll(userId);
	};

	// 스페이스 상세 조회
	public SpaceVO findById(Long spaceId){ return spacesMapper.selectById(spaceId); };

	// 스페이스 입장
	public Long findByCodeAndPw(SpaceVO spaceVO){ return spacesMapper.selectByCodeAndPw(spaceVO); };

	// 스페이스 생성
	public void save(SpaceVO spaceVO){ spacesMapper.insert(spaceVO); };

	// 스페이스 삭제
	public void delete(Long spaceId){ spacesMapper.delete(spaceId); };

	// 스페이스 수정
	public void set(SpaceVO spaceVO){ spacesMapper.update(spaceVO); };

	// 스페이스 회원 전체수
	public int getTally(Long spaceId){ return spacesMapper.getTally(spaceId); };

	// 스페이스 인원 업데이트
	public void setTally(Long spaceId, int spaceUserTally){ spacesMapper.updateTally(spaceId, spaceUserTally); };

	// Post
	// 게시글 필터 조회

}
