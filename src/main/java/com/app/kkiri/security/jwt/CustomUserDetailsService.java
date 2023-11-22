package com.app.kkiri.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.response.UserResponse;
import com.app.kkiri.repository.UsersDAO;
import com.app.kkiri.security.oAuth2Login.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UsersDAO usersDAO;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Long userId = Long.parseLong(username);
		UserResponse userResponseDTO = usersDAO.findById(userId);

		AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
			.userId(userResponseDTO.getUserId())
			.socialType(userResponseDTO.getSocialType())
			.userStatus(userResponseDTO.getUserStatus())
			.accessToken(userResponseDTO.getAccessToken())
			.refreshToken(userResponseDTO.getRefreshToken())
			.userEmail(userResponseDTO.getUserEmail())
			.build();

		return authenticatedUser;
	}
}
