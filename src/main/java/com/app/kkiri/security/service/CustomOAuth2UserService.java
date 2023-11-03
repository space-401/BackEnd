package com.app.kkiri.security.service;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.security.converters.CustomOAuth2UserConverter;
import com.app.kkiri.security.model.AuthenticatedOAuth2User;
import com.app.kkiri.security.model.ProviderUserRequest;
import com.app.kkiri.security.model.CustomOAuth2User;
import com.app.kkiri.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> customOAuth2UserConverter;
	private final UserService userService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		ClientRegistration clientRegistration = userRequest.getClientRegistration();

		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User);

		CustomOAuth2User customOAuth2User = customOAuth2UserConverter.convert(providerUserRequest);
		AuthenticatedOAuth2User authenticatedUser = userService.register(customOAuth2User);

		return authenticatedUser;
	}
}





















