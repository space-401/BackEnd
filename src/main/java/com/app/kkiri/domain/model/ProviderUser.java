package com.app.kkiri.domain.model;

import java.util.Map;

public interface ProviderUser {

	// 사용자 식별 이름
	String getName();

	// 사용자 이메일 주소
	String getEmail();

	// 사용자 속성
	Map<String, Object> getAttributes();
}
