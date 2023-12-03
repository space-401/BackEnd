package com.app.kkiri.repository;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.dto.UserDTO;
import com.app.kkiri.domain.dto.response.UserResponseDTO;
import com.app.kkiri.domain.vo.UserVO;
import com.app.kkiri.mapper.UsersMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UsersDAO {

	private final UsersMapper usersMapper;

	// 다음에 생성할 회원의 회원 고유 번호 조회
	public Long findNextUserId() {
		return usersMapper.selectNextUserId();
	}

	// userId 를 사용하여 회원 조회
	public UserResponseDTO findById(Long userId) {
		UserVO selectedUserVO = usersMapper.selectUser(userId);

		UserResponseDTO userResponseDTO = UserResponseDTO.builder()
			.userId(selectedUserVO.getUserId())
			.socialType(selectedUserVO.getSocialType())
			.userStatus(selectedUserVO.getUserStatus())
			.accessToken(selectedUserVO.getAccessToken())
			.refreshToken(selectedUserVO.getRefreshToken())
			.userEmail(selectedUserVO.getUserEmail())
			.build();

		return userResponseDTO;
	}

	// 회원 가입
	public void save(UserDTO userDTO) {
		UserVO userVO = UserVO.builder()
			.userId(userDTO.getUserId())
			.socialType(userDTO.getSocialType())
			.userStatus(userDTO.getUserStatus())
			.accessToken(userDTO.getAccessToken())
			.refreshToken(userDTO.getRefreshToken())
			.userEmail(userDTO.getUserEmail())
			.build();

		usersMapper.insertUser(userVO);
	}

	// 가장 최근에 가입한 회원의 회원 고유 번호 조회
	public UserResponseDTO findRecentUser() {
		UserVO selectedRecentUser = usersMapper.selectRecentUser();

		UserResponseDTO userResponseDTO = UserResponseDTO.builder()
			.userId(selectedRecentUser.getUserId())
			.socialType(selectedRecentUser.getSocialType())
			.userStatus(selectedRecentUser.getUserStatus())
			.accessToken(selectedRecentUser.getAccessToken())
			.refreshToken(selectedRecentUser.getRefreshToken())
			.userEmail(selectedRecentUser.getUserEmail())
			.build();

		return userResponseDTO;
	}

	// userEmail 을 사용하여 회원 조회
	public UserResponseDTO findByUserEmail(String userEmail) {
		UserVO selectedUserVO = usersMapper.selectUserByUserEmail(userEmail);

		if(selectedUserVO == null) {
			return null;
		}

		UserResponseDTO userResponseDTO = UserResponseDTO.builder()
			.userId(selectedUserVO.getUserId())
			.socialType(selectedUserVO.getSocialType())
			.userStatus(selectedUserVO.getUserStatus())
			.accessToken(selectedUserVO.getAccessToken())
			.refreshToken(selectedUserVO.getRefreshToken())
			.userEmail(selectedUserVO.getUserEmail())
			.build();

		return userResponseDTO;
	}

	// 엑세스 토큰 재발급 후 수정
	public void setAccessToken(Long userId, String accessToken) {
		usersMapper.updateAccessToken(userId, accessToken);
	}

	// 엑세스 토큰과 리프레쉬 토큰 재발급 후 수정
	public void setTokens(Long userId, String accessToken, String refreshToken) {
		usersMapper.updateAccessTokenAndRefreshToken(userId, accessToken, refreshToken);
	}

	// 회원 탈퇴
	public void deleteUser(Long userId) {
		usersMapper.updateUserStatus(userId);
	}
}
