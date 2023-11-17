package com.app.kkiri.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/**");
	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException, IOException, ServletException {

		AntPathRequestMatcher rootPath = new AntPathRequestMatcher("/");
		AntPathRequestMatcher socialLoginPath = new AntPathRequestMatcher("/user/auth/*");

		// 루트 경로와 소셜 로그인 경로를 제외한 모든 경로에 대해서만 필터를 수행한다.
		if(rootPath.matches(request) || socialLoginPath.matches(request)) {
			return new JwtAuthenticationToken("");
		}

		String jwt = jwtTokenProvider.resolveToken(request);
		if(jwt == null) {
			OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
			throw new OAuth2AuthenticationException(oauth2Error, "올바른 형식의 토큰이 아닙니다");
		}

		JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
		LOGGER.info("[attemptAuthentication()] value jwtAuthenticationToken : {}", jwtAuthenticationToken);

		jwtAuthenticationToken = (JwtAuthenticationToken)super.getAuthenticationManager().authenticate(jwtAuthenticationToken);
		LOGGER.info("[attemptAuthentication()] value jwtAuthenticationToken : {}", jwtAuthenticationToken);

		Assert.notNull(jwtAuthenticationToken, "authentication result cannot be null");

		return jwtAuthenticationToken;
	}
}