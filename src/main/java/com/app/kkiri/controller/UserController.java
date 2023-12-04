package com.app.kkiri.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.app.kkiri.domain.dto.response.BookmarkedPostListResponseDTO;
import com.app.kkiri.domain.dto.response.MyPostListResponseDTO;
import com.app.kkiri.domain.dto.response.UserMypageResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/mypage")
	public ResponseEntity<?> mypage(HttpServletRequest request) {

		UserMypageResponseDTO userMypageResponseDTO = userService.searchMypage(jwtTokenProvider.getUserIdByHttpRequest(request));

		return ResponseEntity.ok().body(userMypageResponseDTO);
	}

	@DeleteMapping("")
	public ResponseEntity<?> deleteUser(HttpServletRequest httpServletRequest) {

		Long userId = jwtTokenProvider.getUserIdByHttpRequest(httpServletRequest);

		postService.deleteByUserId(userId);
		spaceService.deleteSpace(userId);
		spaceService.deleteSpaceUser(userId);
		userService.deleteUser(userId);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/bookmark")
	public ResponseEntity<BookmarkedPostListResponseDTO> bookmark(@RequestParam int page, HttpServletRequest httpServletRequest) {

		return ResponseEntity.ok().body(userService.bookmarkList(jwtTokenProvider.getUserIdByHttpRequest(httpServletRequest), page));
	}

	@GetMapping("/mypost")
	public ResponseEntity<MyPostListResponseDTO> mypost(@RequestParam int page, HttpServletRequest httpServletRequest) {

		return ResponseEntity.ok().body(userService.myPostList(jwtTokenProvider.getUserIdByHttpRequest(httpServletRequest), page));
	}
}
