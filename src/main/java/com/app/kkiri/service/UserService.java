package com.app.kkiri.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.security.model.AuthenticatedOAuth2User;
import com.app.kkiri.security.model.CustomOAuth2User;
import com.app.kkiri.security.utils.JwtTokenProvider;
import com.app.kkiri.security.enums.UserStatus;
import com.app.kkiri.domain.dto.UserDTO;
import com.app.kkiri.domain.dto.UserResponseDTO;
import com.app.kkiri.repository.UsersDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	private final UsersDAO usersDAO;
	private final JwtTokenProvider jwtTokenProvider;

	// userId 를 사용하여 회원 조회
	@Transactional(rollbackFor = Exception.class)
	public UserResponseDTO search(Long userId) {
		return usersDAO.findById(userId);
	}

	// 회원 가입 및 로그인
	@Transactional(rollbackFor = Exception.class)
	public AuthenticatedOAuth2User register(CustomOAuth2User customOAuth2User) {

		LOGGER.info("register() param customOAuth2User : {}", customOAuth2User);

		String userEmail = customOAuth2User.getEmail();
		Long nextUserId = usersDAO.findNextUserId();
		String accessToken = jwtTokenProvider.createAccessToken(nextUserId);
		String refreshToken = jwtTokenProvider.createRefreshToken(nextUserId);

		UserResponseDTO userResponseDTO = null;

		// 신규 회원인 경우 회원 가입을 한다
		if(usersDAO.findByUserEmail(userEmail) == null) {
			UserDTO userDTO = UserDTO.builder()
				.userId(nextUserId)
				.socialType(customOAuth2User.getRegistrationId())
				.userStatus(UserStatus.USER.getUserStatus())
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.userEmail(userEmail)
				.build();

			usersDAO.save(userDTO);

			userResponseDTO = usersDAO.findRecentUser();
		}
		LOGGER.info("register() returned value userResponseDTO : {}", userResponseDTO);

		// 기존 회원인 경우
		userResponseDTO = usersDAO.findByUserEmail(userEmail);
		LOGGER.info("register() returned value userResponseDTO : {}", userResponseDTO);

		// 리프레쉬 토큰이 만료되어 다시 소셜 로그인을 하는 경우
		if(!jwtTokenProvider.validateToken(userResponseDTO.getRefreshToken())) {

			Long selectedUserId = userResponseDTO.getUserId();
			String newAccessToken = jwtTokenProvider.createAccessToken(selectedUserId);
			String newRefreshToken = jwtTokenProvider.createRefreshToken(selectedUserId);
			usersDAO.setTokens(selectedUserId, newAccessToken, newRefreshToken);

			userResponseDTO = usersDAO.findById(selectedUserId);
		}
		LOGGER.info("register() returned value userResponseDTO : {}", userResponseDTO);

		AuthenticatedOAuth2User authenticatedOAuth2User = AuthenticatedOAuth2User.builder()
			.userId(userResponseDTO.getUserId())
			.socialType(userResponseDTO.getSocialType())
			.userStatus(userResponseDTO.getUserStatus())
			.accessToken(userResponseDTO.getAccessToken())
			.refreshToken(userResponseDTO.getRefreshToken())
			.userEmail(userResponseDTO.getUserEmail())
			.build();
		LOGGER.info("register() returned value authenticatedOAuth2User : {}", authenticatedOAuth2User);

		return authenticatedOAuth2User;
	}

	@Transactional(rollbackFor = Exception.class)
	public String reissueAccessToken(String refreshToken) {

		Long userId = jwtTokenProvider.getUserId(refreshToken);
		if(userId == null) {
			throw new UsernameNotFoundException("존재하지 않는 사용자 입니다");
		}

	 	String newAccessToken = jwtTokenProvider.createAccessToken(userId);
		usersDAO.setAccessToken(userId, newAccessToken);

		return newAccessToken;
	}
}































