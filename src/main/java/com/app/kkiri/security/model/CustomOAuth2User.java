package com.app.kkiri.security.model;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface CustomOAuth2User extends OAuth2User {

	// 사용자 식별 이름
	String getName();

	// 사용자 이메일 주소
	String getEmail();

	// 사용자 속성
	Map<String, Object> getAttributes();

	// 사용자 Registration Id
	String getRegistrationId();
}
