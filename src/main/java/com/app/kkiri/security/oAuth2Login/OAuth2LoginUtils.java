package com.app.kkiri.security.oAuth2Login;

import java.util.Map;

import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2LoginUtils {

	public static Attributes createMainAttributes(OAuth2User oAuth2User) {

		return Attributes.builder()
			.mainAttributes(oAuth2User.getAttributes())
			.build();
	}

	public static Attributes createSubAttributes(OAuth2User oAuth2User, String subAttributesKey) {

		Map<String, Object> subAttributes = (Map<String, Object>) oAuth2User.getAttributes().get(subAttributesKey);
		return Attributes.builder()
			.subAttributes(subAttributes)
			.build();
	}

	public static Attributes createOtherAttributes(OAuth2User oAuth2User, String subAttributesKey, String otherAttributesKey) {

		Map<String, Object> subAttributes = (Map<String, Object>) oAuth2User.getAttributes().get(subAttributesKey);
		Map<String, Object> otherAttributes = (Map<String, Object>) subAttributes.get(otherAttributesKey);

		return Attributes.builder()
			.otherAttributes(otherAttributes)
			.build();
	}
}