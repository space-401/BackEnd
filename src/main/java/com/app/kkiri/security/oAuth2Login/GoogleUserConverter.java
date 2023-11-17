package com.app.kkiri.security.oAuth2Login;

import com.app.kkiri.security.enums.SocialType;

public class GoogleUserConverter implements CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> {
	@Override
	public CustomOAuth2User convert(ProviderUserRequest providerUserRequest) {

		if(!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.GOOGLE.getSocialType())) {
			return null;
		}

		GoogleUser googleUser = new GoogleUser(OAuth2LoginUtils.createMainAttributes(providerUserRequest.getOAuth2User())
			, providerUserRequest.getOAuth2User()
			, providerUserRequest.getClientRegistration());

		return googleUser;
	}
}
