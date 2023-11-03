package com.app.kkiri.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.kkiri.common.Response;
import com.google.gson.Gson;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			Gson gson = new Gson();

			Response entryPointErrorResponse = new Response();
			entryPointErrorResponse.setMessage("인증에 실패하였습니다");

			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write(gson.toJson(entryPointErrorResponse));
			response.setContentType("application/json");
		}
	}
}
