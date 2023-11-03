package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.app.kkiri.common.Response;
import com.google.gson.Gson;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		Gson gson = new Gson();

		Response entryPointErrorResponse = new Response();
		entryPointErrorResponse.setMessage("인증에 실패하였습니다");

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.getWriter().write(gson.toJson(entryPointErrorResponse));
		response.setContentType("application/json");
	}
}
