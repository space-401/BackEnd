package com.app.kkiri.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	@GetMapping("/success")
	public ResponseEntity<OAuth2User> success(Authentication authentication) {

		return ResponseEntity.status(HttpStatus.OK).body((OAuth2User)authentication.getPrincipal());
	}

	@GetMapping("/failure")
	public ResponseEntity failure() {

		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
