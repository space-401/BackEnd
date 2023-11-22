package com.app.kkiri.security.jwt;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Component;

import com.app.kkiri.domain.dto.response.UserResponse;
import com.app.kkiri.repository.UsersDAO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
	private final JwtTokenProvider jwtTokenProvider;
	private final UsersDAO usersDAO;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException{

		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)authentication;
		LOGGER.info("[authenticate()] param jwtAuthenticationToken : {}", jwtAuthenticationToken);

		String accessToken = jwtAuthenticationToken.getJwt();
		if(!jwtTokenProvider.validateToken(accessToken)) {
			OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN);
			throw new OAuth2AuthenticationException(oauth2Error, "만료된 토큰입니다");
		}

		Long userId = jwtTokenProvider.getUserIdByToken(accessToken);
		UserResponse userResponseDTO = usersDAO.findById(userId);
		if(userResponseDTO == null) {
			OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT);
			throw new OAuth2AuthenticationException(oauth2Error, "존재하지 않는 사용자입니다");
		}

		Map<String, Object> attributes = new HashMap<>();
		attributes.put("userId", userResponseDTO.getUserId());
		attributes.put("socialType", userResponseDTO.getSocialType());
		attributes.put("userStatus", userResponseDTO.getUserStatus());
		attributes.put("accessToken", userResponseDTO.getAccessToken());
		attributes.put("refreshToken", userResponseDTO.getRefreshToken());
		attributes.put("userEmail", userResponseDTO.getUserEmail());

		DefaultJwtUser defaultJwtUser = new DefaultJwtUser(attributes);
		jwtAuthenticationToken = new JwtAuthenticationToken(defaultJwtUser);
		LOGGER.info("[authenticate()] value jwtAuthenticationToken : {}", jwtAuthenticationToken);

		return jwtAuthenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
