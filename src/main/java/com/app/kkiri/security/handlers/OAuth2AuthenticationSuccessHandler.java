package com.app.kkiri.security.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.kkiri.security.model.AuthenticatedUser;
import com.google.gson.Gson;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		Gson gson = new Gson();

		AuthenticatedUser authenticatedUser = (AuthenticatedUser)authentication.getPrincipal();
		String json = gson.toJson(authenticatedUser.getUserResponseDTO());

		response.setStatus(HttpStatus.OK.value());
		response.getWriter().write(json);
		response.flushBuffer();
	}
}
