package com.app.kkiri.security.oAuth2Login;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class DelegationCustomOAuth2UserConverter implements CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> {
	private final List<CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User>> converters =
		Arrays.asList(new GoogleUserConverter(), new NaverUserConverter(), new KakaoUserConverter());

	@Override
	public CustomOAuth2User convert(ProviderUserRequest providerUserRequest) {
		Assert.notNull(providerUserRequest, "providerUserRequest cannot be null");

		for(CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> converter : this.converters) {
			CustomOAuth2User customOAuth2User = converter.convert(providerUserRequest);

			if(customOAuth2User != null) {
				return customOAuth2User;
			}
		}

		return null;
	}
}
