package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.kkiri.global.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.app.kkiri.security.Response;
import com.google.gson.Gson;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		Gson gson = new Gson();
		ExceptionResponse exceptionResponse = new ExceptionResponse(1000, "올바르지 않은 요청입니다.");
		response.setStatus(HttpStatus.BAD_REQUEST.value());

		if(authException instanceof OAuth2AuthenticationException) {
			OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException)authException;
			String errorCode = oAuth2AuthenticationException.getError().getErrorCode();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());

			switch (errorCode) {
				case OAuth2ErrorCodes.INVALID_TOKEN:
					exceptionResponse = new ExceptionResponse(9106, oAuth2AuthenticationException.getMessage());
					break;

				case OAuth2ErrorCodes.INVALID_CLIENT:
					exceptionResponse = new ExceptionResponse(9107, oAuth2AuthenticationException.getMessage());
					break;
			}
		}

		response.getWriter().write(gson.toJson(exceptionResponse));
		response.setContentType("application/json");
	}
}
