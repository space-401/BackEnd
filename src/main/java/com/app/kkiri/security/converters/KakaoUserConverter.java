package com.app.kkiri.security.converters;

import com.app.kkiri.security.enums.SocialType;
import com.app.kkiri.security.utils.OAuth2LoginUtils;
import com.app.kkiri.security.model.ProviderUserRequest;
import com.app.kkiri.security.model.KakaoUser;
import com.app.kkiri.security.model.CustomOAuth2User;

public class KakaoUserConverter implements CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> {
	@Override
	public CustomOAuth2User convert(ProviderUserRequest providerUserRequest) {

		if(!providerUserRequest.getClientRegistration().getRegistrationId().equals(SocialType.KAKAO.getSocialType())) {
			return null;
		}

		KakaoUser kakaoUserUser = new KakaoUser(OAuth2LoginUtils.createSubAttributes(providerUserRequest.getOAuth2User(), "kakao_account")
			, providerUserRequest.getOAuth2User()
			, providerUserRequest.getClientRegistration());

		return kakaoUserUser;
	}
}
