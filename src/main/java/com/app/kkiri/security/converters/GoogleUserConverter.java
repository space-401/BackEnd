package com.app.kkiri.security.converters;

import com.app.kkiri.security.enums.SocialType;
import com.app.kkiri.security.model.ProviderUserRequest;
import com.app.kkiri.security.model.GoogleUser;
import com.app.kkiri.security.model.CustomOAuth2User;
import com.app.kkiri.security.utils.OAuth2LoginUtils;

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
