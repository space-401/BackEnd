package com.app.kkiri.security.oAuth2Login;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.ToString;

@ToString
public abstract class AbstractCustomOAuth2User implements CustomOAuth2User {

	private Map<String, Object> attributes;
	private OAuth2User oAuth2User;
	private ClientRegistration clientRegistration;

	public AbstractCustomOAuth2User(Map<String, Object> attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
		this.attributes = attributes;
		this.oAuth2User = oAuth2User;
		this.clientRegistration = clientRegistration;
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getRegistrationId() {
		return clientRegistration.getRegistrationId();
	}
}
