package com.app.kkiri.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.vo.UserVO;

@Mapper
public interface UsersMapper {
	// 다음에 생성할 회원의 회원 고유 번호 조회
	Long selectNextUserId();

	// userId 를 사용하여 회원 조회
	UserVO selectUser(Long userId);

	// 회원 가입
	void insertUser(UserVO userVO);

	// 가장 최근에 가입한 회원의 회원 고유 번호 조회
	UserVO selectRecentUser();

	// userEmail 을 사용하여 회원 조회
	UserVO selectUserByUserEmail(String userEmail);

	// // 목록 조회
	// public List<SpaceVO> selectAll(Long userId);
	//
	// // 스페이스 상세 조회
	// public SpaceVO selectById(Long spaceId);
	//
	// // 스페이스 입장
	// public Long selectByCodeAndPw(SpaceVO spaceVO);
	//
	// // 스페이스 생성
	// public void insert(SpaceVO spaceVO);
	//
	// // 스페이스 삭제
	// public void delete(Long spaceId);
	//
	// // 스페이스 수정
	// public void update(SpaceDTO spaceDTO);
	//
	// // 스페이스 회원 전체수
	//  public int getTally(Long spaceId);
	//
	// // 스페이스 인원 업데이트
	// public void updateTally(Long spaceId, int spaceUserTally);
}
