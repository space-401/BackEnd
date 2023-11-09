package com.app.kkiri.security.oAuth2Login;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticatedOAuth2User implements OAuth2User {

	private Long userId;
	private String socialType;
	private String userStatus;
	private String accessToken;
	private String refreshToken;
	private String userEmail;

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getName() {
		return String.valueOf(this.userId);
	}
}
