package com.app.kkiri.security.oAuth2Login;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleUser extends AbstractCustomOAuth2User {
	public GoogleUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
		super(attributes.getMainAttributes(), oAuth2User, clientRegistration);
	}

	@Override
	public String getName() {
		return (String)getAttributes().get("name");
	}
}
