package com.app.kkiri.security.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SocialType {
	GOOGLE("google"),
	NAVER("naver"),
	KAKAO("kakao"),
	;

	private final String socialType;
}
