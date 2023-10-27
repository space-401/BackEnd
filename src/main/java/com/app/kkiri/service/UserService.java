package com.app.kkiri.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.app.kkiri.security.model.AuthenticatedUser;
import com.app.kkiri.security.model.CustomOAuth2User;
import com.app.kkiri.security.utils.JwtTokenProvider;
import com.app.kkiri.security.enums.UserStatus;
import com.app.kkiri.domain.dto.UserDTO;
import com.app.kkiri.domain.dto.UserResponseDTO;
import com.app.kkiri.repository.UsersDAO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	private final UsersDAO usersDAO;

	private final JwtTokenProvider jwtTokenProvider;

	// userId 를 사용하여 회원 조회
	public UserResponseDTO search(Long userId) {
		return usersDAO.findById(userId);
	}

	// 회원 가입
	public AuthenticatedUser register(CustomOAuth2User customOAuth2User) {
		String userEmail = customOAuth2User.getEmail();
		Long nextUserId = usersDAO.findNextUserId();
		String accessToken = jwtTokenProvider.createAccessToken(nextUserId);
		String refreshToken = jwtTokenProvider.createRefreshToken(nextUserId);

		UserResponseDTO userResponseDTO;

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
		userResponseDTO = usersDAO.findByUserEmail(userEmail);

		return AuthenticatedUser.builder().userResponseDTO(userResponseDTO).build();
	}
}
