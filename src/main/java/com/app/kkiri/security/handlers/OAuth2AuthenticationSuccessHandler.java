package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.kkiri.domain.dto.UserResponseDTO;
import com.app.kkiri.security.model.AuthenticatedOAuth2User;
import com.google.gson.Gson;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		Gson gson = new Gson();
		AuthenticatedOAuth2User authenticatedUser = (AuthenticatedOAuth2User)authentication.getPrincipal();
		UserResponseDTO userResponseDTO = UserResponseDTO.builder()
			.userId(authenticatedUser.getUserId())
			.socialType(authenticatedUser.getSocialType())
			.userStatus(authenticatedUser.getUserStatus())
			.accessToken(authenticatedUser.getAccessToken())
			.refreshToken(authenticatedUser.getRefreshToken())
			.userEmail(authenticatedUser.getUserEmail())
			.build();

		response.setStatus(HttpStatus.ACCEPTED.value());
		response.getWriter().write(gson.toJson(userResponseDTO));
		// ?? 성공적인 인증을 마치면 세션에 이미 저장되어 있는 이전 요청 정보를 추출해서 이전 요청 위치로 리다이렉트 해야하나 ??
	}
}
