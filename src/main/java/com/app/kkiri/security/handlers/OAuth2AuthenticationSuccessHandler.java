package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.kkiri.domain.dto.response.UserResponse;
import com.app.kkiri.security.oAuth2Login.AuthenticatedOAuth2User;
import com.google.gson.Gson;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		Gson gson = new Gson();
		AuthenticatedOAuth2User authenticatedUser = (AuthenticatedOAuth2User)authentication.getPrincipal();
		UserResponse userResponseDTO = UserResponse.builder()
			.userId(authenticatedUser.getUserId())
			.socialType(authenticatedUser.getSocialType())
			.userStatus(authenticatedUser.getUserStatus())
			.accessToken(authenticatedUser.getAccessToken())
			.refreshToken(authenticatedUser.getRefreshToken())
			.userEmail(authenticatedUser.getUserEmail())
			.build();

		response.setStatus(HttpStatus.ACCEPTED.value());
		response.getWriter().write(gson.toJson(userResponseDTO));
		response.setContentType("application/json");
	}
}
