package com.app.kkiri.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.service.PostService;
import com.app.kkiri.service.SpaceService;
import com.app.kkiri.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserService userService;
	private final SpaceService spaceService;
	private final PostService postService;
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/refreshToken")
	public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest httpServletRequest) {

		String reissuedAccessToken = jwtTokenProvider.reissueAccessToken(httpServletRequest);
		userService.updateAccessToken(reissuedAccessToken);

		Map<String, Object> map = new HashMap<>();
		map.put("newAccessToken", reissuedAccessToken);

		return ResponseEntity.ok().body(map);
	}

	@DeleteMapping("")
	public ResponseEntity<?> deleteUser(HttpServletRequest httpServletRequest) {

		userService.deleteUser(httpServletRequest);
		spaceService.deleteSpaceUser(httpServletRequest);
		postService.deleteByUserId(httpServletRequest);

		return ResponseEntity.noContent().build();
	}
}
