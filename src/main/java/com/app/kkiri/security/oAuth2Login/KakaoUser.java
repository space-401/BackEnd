package com.app.kkiri.security.oAuth2Login;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class KakaoUser extends AbstractCustomOAuth2User {
	private Map<String, Object> otherAttributes;

	public KakaoUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
		super(attributes.getSubAttributes(), oAuth2User, clientRegistration);

		otherAttributes =
			OAuth2LoginUtils.createOtherAttributes(oAuth2User, "kakao_account", "profile").getOtherAttributes();
	}

	@Override
	public String getName() {
		return (String)otherAttributes.get("nickname");
	}
}
