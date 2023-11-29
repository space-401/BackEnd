package com.app.kkiri.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.UserDTO;
import com.app.kkiri.domain.dto.response.UserResponseDTO;
import com.app.kkiri.repository.UsersDAO;
import com.app.kkiri.security.enums.UserStatus;
import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.security.oAuth2Login.AuthenticatedOAuth2User;
import com.app.kkiri.security.oAuth2Login.CustomOAuth2User;

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
	public AuthenticatedOAuth2User register(CustomOAuth2User customOAuth2User) throws OAuth2AuthenticationException {
		LOGGER.info("register() [param] customOAuth2User.getName() : {}", customOAuth2User.getName());
		LOGGER.info("register() [param] customOAuth2User.getEmail() : {}", customOAuth2User.getEmail());

		// 토큰에 이메일 데이터가 없는 경우
		String userEmail = customOAuth2User.getEmail();
		if(userEmail == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN), "토큰에 이메일 주소가 포함되어 있지 않습니다");
		}

		UserResponseDTO selectedUser = usersDAO.findByUserEmail(userEmail);
		String socialType = customOAuth2User.getRegistrationId();

		// 동일한 이메일 주소로 다른 소셜 로그인을 시도하는 경우
		if(selectedUser != null && userEmail.equals(selectedUser.getUserEmail()) && !socialType.equals(selectedUser.getSocialType())) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT), "이미 가입된 이메일 주소입니다");
		}

		UserResponseDTO userResponseDTO = null;

		if(selectedUser == null) { // 신규 회원인 경우 회원 가입을 한다
			Long nextUserId = usersDAO.findNextUserId();
			String accessToken = jwtTokenProvider.createAccessToken(nextUserId);
			String refreshToken = jwtTokenProvider.createRefreshToken(nextUserId);

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
			LOGGER.info("register() [if 신규 회원인 경우 result] userResponseDTO : {}", userResponseDTO);
		} else { // 기존 회원인 경우
			userResponseDTO = usersDAO.findByUserEmail(userEmail);
			LOGGER.info("register() [if 기존 회원인 경우 start] userResponseDTO : {}", userResponseDTO);

			// 리프레쉬 토큰이 만료되어 다시 소셜 로그인을 하는 경우
			if(!jwtTokenProvider.validateToken(userResponseDTO.getRefreshToken())) {
				Long selectedUserId = userResponseDTO.getUserId();

				String newAccessToken = jwtTokenProvider.createAccessToken(selectedUserId);
				String newRefreshToken = jwtTokenProvider.createRefreshToken(selectedUserId);
				usersDAO.setTokens(selectedUserId, newAccessToken, newRefreshToken);

				userResponseDTO = usersDAO.findById(selectedUserId);
				LOGGER.info("register() [if 기존 회원인 경우 end] userResponseDTO : {}", userResponseDTO);
			}
		}

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
	public void updateAccessToken(String reissuedAccessToken) throws AuthenticationException {
		LOGGER.info("updateAccessToken() [param] reissuedAccessToken : {}", reissuedAccessToken);

		Long userId = jwtTokenProvider.getUserIdByToken(reissuedAccessToken);

		usersDAO.setAccessToken(userId, reissuedAccessToken);
	}
}































