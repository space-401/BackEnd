package com.app.kkiri.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/*")
@RequiredArgsConstructor
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;

	@GetMapping("/refreshToken")
	public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest httpServletRequest) throws AuthenticationException, IOException {

		String refreshToken = jwtTokenProvider.resolveToken(httpServletRequest);
		if(refreshToken == null) {
			OAuth2Error oAuth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
			throw new OAuth2AuthenticationException(oAuth2Error, "헤더 정보가 잘못되었습니다");
		}
		LOGGER.info("refreshToken() value refreshToken : {}", refreshToken);

		if(!jwtTokenProvider.validateToken(refreshToken)) {
			OAuth2Error oAuth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN);
			throw new OAuth2AuthenticationException(oAuth2Error, "유효하지 않은 토큰입니다");
		}

		String newAccessToken = userService.reissueAccessToken(refreshToken);
		LOGGER.info("refreshToken() value newAccessToken : {}", newAccessToken);

		Map<String, Object> map = new HashMap<>();
		map.put("newAccessToken", newAccessToken);

		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
}
