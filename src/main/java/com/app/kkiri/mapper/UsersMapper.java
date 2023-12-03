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

	// 엑세스 토큰 재발급 후 수정
	void updateAccessToken(Long userId, String accessToken);

	// 엑세스 토큰과 리프레쉬 토큰 재발급 후 수정
	void updateAccessTokenAndRefreshToken(Long userId, String accessToken, String refreshToken);

	// 회원 탈퇴
	void updateUserStatus(Long userId);

}
