package com.app.kkiri.security.oAuth2Login;

import com.app.kkiri.security.enums.SocialType;

public class NaverUserConverter implements CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> {
	@Override
	public CustomOAuth2User convert(ProviderUserRequest providerUserRequest) {

		if(!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.NAVER.getSocialType())) {
			return null;
		}

		NaverUser naverUser = new NaverUser(
			OAuth2LoginUtils.createSubAttributes(providerUserRequest.getOAuth2User(), "response")
			, providerUserRequest.getOAuth2User()
			, providerUserRequest.getClientRegistration());

		return naverUser;
	}
}
