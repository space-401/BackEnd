package com.app.kkiri.security.model;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class NaverUser extends AbstractCustomOAuth2User {

	public NaverUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
		super(attributes.getSubAttributes(), oAuth2User, clientRegistration);
	}

	@Override
	public String getName() {
		return (String)getAttributes().get("name");
	}
}
