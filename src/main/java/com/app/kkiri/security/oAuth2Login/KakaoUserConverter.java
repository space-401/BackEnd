package com.app.kkiri.security.oAuth2Login;

import com.app.kkiri.security.enums.SocialType;

public class KakaoUserConverter implements CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> {
	@Override
	public CustomOAuth2User convert(ProviderUserRequest providerUserRequest) {

		if(!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.KAKAO.getSocialType())) {
			return null;
		}

		KakaoUser kakaoUserUser = new KakaoUser(
			OAuth2LoginUtils.createSubAttributes(providerUserRequest.getOAuth2User(), "kakao_account")
			, providerUserRequest.getOAuth2User()
			, providerUserRequest.getClientRegistration());

		return kakaoUserUser;
	}
}
