package com.app.kkiri.repository;

import com.app.kkiri.mapper.SpaceUsersMapper;
import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.vo.SpaceUserVO;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpaceUsersDAO {
	private final SpaceUsersMapper spaceUsersMapper;
	// 스페이스 회원 목록
	public List<SpaceUserVO> findAll(Long spaceId, Long userId){ return spaceUsersMapper.selectAll(spaceId, userId); };

	// 스페이스내 내 정보 조회
	public SpaceUserVO findById(Long spaceId, Long userId){ return spaceUsersMapper.selectById(spaceId, userId); };

	// 스페이스 회원 상태 체크
	public int findByUserAdminYn(Long spaceId, Long userId){ return spaceUsersMapper.selectByUserAdminYn(spaceId, userId); };

	// 스페이스 첫 방문 체크
	public int findByFirst(Long spaceId, Long userId){ return spaceUsersMapper.selectByFirst(spaceId, userId); };

	// 스페이스 회원 입장 (추가)
	public void save(SpaceUserVO spaceUserVO){ spaceUsersMapper.insert(spaceUserVO); };

	// 스페이스 회원 탈퇴
	public void delete(Long spaceId, Long userId){ spaceUsersMapper.delete(spaceId, userId); };

	// 스페이스 내 유저 정보 변경
	public void set(SpaceUserVO spaceUserVO){ spaceUsersMapper.update(spaceUserVO); };

	// 일반 회원으로 변경
	public void setByAdminYn(Long spaceId){ spaceUsersMapper.updateByAdminYn(spaceId); };

	// 방장 권한 부여
	public void setByUserId(Long spaceId, Long userId){ spaceUsersMapper.updateByUserId(spaceId, userId); };

}
