package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.app.kkiri.common.Response;
import com.google.gson.Gson;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {

		OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException)authException;

		Gson gson = new Gson();
		Response oAuth2AuthenticationFailureHandlerResponse = new Response();

		oAuth2AuthenticationFailureHandlerResponse.setMessage(oAuth2AuthenticationException.getMessage());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		response.getWriter().write(gson.toJson(oAuth2AuthenticationFailureHandlerResponse));
		response.setContentType("application/json");
	}
}
