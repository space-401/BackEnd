package com.app.kkiri.security.converters;

import com.app.kkiri.security.enums.SocialType;
import com.app.kkiri.security.model.ProviderUserRequest;
import com.app.kkiri.security.model.NaverUser;
import com.app.kkiri.security.model.CustomOAuth2User;
import com.app.kkiri.security.utils.OAuth2LoginUtils;

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
