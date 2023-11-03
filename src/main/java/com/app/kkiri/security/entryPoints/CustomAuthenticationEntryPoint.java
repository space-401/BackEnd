package com.app.kkiri.security.entryPoints;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.app.kkiri.common.Response;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		Gson gson = new Gson();

		Response entryPointErrorResponse = new Response();
		entryPointErrorResponse.setMessage("인증에 실패하였습니다");

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write(gson.toJson(entryPointErrorResponse));
	}
}
