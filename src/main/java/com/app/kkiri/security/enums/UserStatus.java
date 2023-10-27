package com.app.kkiri.security.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserStatus {
	USER("user"),
	VISITOR("visitor"),
	LEAVER("leaver");

	private final String userStatus;
}
