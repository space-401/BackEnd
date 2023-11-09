package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.app.kkiri.common.Response;
import com.google.gson.Gson;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		Gson gson = new Gson();
		Response entryPointErrorResponse = new Response();

		if(authException instanceof OAuth2AuthenticationException) {
			OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException)authException;
			entryPointErrorResponse.setMessage(oAuth2AuthenticationException.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}

		if(authException instanceof InsufficientAuthenticationException) {
			// entryPointErrorResponse.setMessage("서버 에러가 발생했습니다");
			// response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			entryPointErrorResponse.setMessage(authException.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}

		response.getWriter().write(gson.toJson(entryPointErrorResponse));
		response.setContentType("application/json");
	}
}
