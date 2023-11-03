package com.app.kkiri.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.kkiri.security.model.AuthenticatedUser;
import com.app.kkiri.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user/*")
@RequiredArgsConstructor
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	@GetMapping("/refreshToken")
	public ResponseEntity<Map<String, Object>> refreshToken(Authentication authentication) {
		LOGGER.info("refreshToken() param authentication : {}", authentication);

		AuthenticatedUser oAuth2User = (AuthenticatedUser)authentication.getPrincipal();
		String refreshToken = oAuth2User.getRefreshToken();
		LOGGER.info("refreshToken() returned value refreshToken : {}", refreshToken);

		String newAccessToken = userService.reissueAccessToken(refreshToken);
		LOGGER.info("refreshToken() returned value newAccessToken : {}", newAccessToken);

		Map<String, Object> map = new HashMap<>();
		map.put("newAccessToken", newAccessToken);
		map.forEach((key, value) -> {
			LOGGER.info("refreshToken() returned value key : {}, value : {}", key, value);
		});

		return ResponseEntity.status(HttpStatus.OK.value()).body(map);
	}

	@ExceptionHandler(value = UsernameNotFoundException.class)
	public ResponseEntity<Map<String, Object>> UsernameNotFoundExceptionHandler(RuntimeException e) {

		Map<String, Object> map = new HashMap<>();
		map.put("message", e.getMessage());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(map);
	}
}
