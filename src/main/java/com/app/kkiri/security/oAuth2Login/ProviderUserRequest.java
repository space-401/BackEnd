package com.app.kkiri.security.oAuth2Login;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class ProviderUserRequest {

	private final ClientRegistration clientRegistration;
	private final OAuth2User oAuth2User;
}
